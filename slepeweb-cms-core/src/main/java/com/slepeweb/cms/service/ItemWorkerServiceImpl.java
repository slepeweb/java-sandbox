package com.slepeweb.cms.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.FieldValueSet;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.MoverItem;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.NotRevertableException;
import com.slepeweb.cms.except.NotVersionableException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.LogUtil;

/*
 * This service offloads some of the heavy-lifting that was originally located
 * in ItemServiceImpl. It has been moved to this file, so that ItemServiceImpl
 * can concentrate on JDBC instructions.
 */

@Service
public class ItemWorkerServiceImpl implements ItemWorkerService {
	private static Logger LOG = Logger.getLogger(ItemWorkerServiceImpl.class);

	@Autowired private ItemService itemService;
	@Autowired private LinkService linkService;
	@Autowired private MediaService mediaService;
	@Autowired private SolrService4Cms solrService4Cms;
	@Autowired private FieldForTypeService fieldForTypeService;
	@Autowired private TagService tagService;

	/*
	 * This provides a relative move, ie before/after target.
	 * If mode == "over", then target is effectively a new parent.
	 */
	public MoverItem move(MoverItem mover) throws ResourceException {
		
		if (! mover.isValid()) {
			throw new ResourceException("Missing item data for move");
		}
		
		String mode = mover.getTo().getMode();
		Item target = this.itemService.getItemByOriginalId(mover.getTo().getTargetId());
		if (target == null) {
			throw new ResourceException("Target of move not found");
		}
		
		Item newParent = mode.equals(MoverItem.MOVE_OVER) ? target : target.getParent();
		if (newParent == null) {
			throw new ResourceException("Attempted to move an item to one of its descendants");
		}
		
		LOG.info(String.format("Moving [%s] (mover) %s [%s] (target)", mover, /*to.getMode()*/ mode.toUpperCase(), target));			
		
		// Cannot move an item to one of its descendants
		if (newParent.getPath().startsWith(mover.getPath())) {
			throw new ResourceException("Cannot move an item to one of its descendants");
		}
		
		// Cannot create a binding to a parent when the same item is already linked
		// to the parent as an inline/relation/shortcut.
		for (Link l : newParent.getAllLinksBarBindings()) {
			if (l.getChild().equalsId(mover)) {
				throw new ResourceException("This item is already linked to the new parent as a relation/inline/shortcut");
			}
		}
		
		// Break the parent link for the mover, EVEN IF old-parent = new-parent
		this.linkService.deleteLinks(mover.getParent().getId(), mover.getId());
		LOG.debug("  Removed links between mover and old parent");		
		
		// Bind to new parent - we'll save() the mover link later
		Link moverLink = CmsBeanFactory.makeLink().
				setParentId(newParent.getId()).
				setChild(mover).
				setType(LinkType.binding).
				setName("std");
		
		// Add mover to new parent's bindings
		List<Link> bindings = newParent.getBindings();
		
		if (mode.equals(MoverItem.MOVE_OVER)) {
			bindings.add(moverLink);
			LOG.info("  Added mover to end of new parent's existing bindings");	
		}
		else {
			// If mode is 'before' or 'after', identify insertions point and re-order all siblings
			int cursor = -1;
			for (Link l : bindings) {
				if (l.getChild().getId().equals(target.getId())) {
					cursor = bindings.indexOf(l);
					break;
				}
			}
			
			// Now insert the mover into the bindings list
			if (cursor > -1) {
				if (mode.equals(MoverItem.MOVE_BEFORE)) {
					bindings.add(cursor, moverLink);
				}
				else if (mode.equals(MoverItem.MOVE_AFTER)) {
					if (cursor < bindings.size()) {
						bindings.add(cursor + 1, moverLink);
					}
					else {
						bindings.add(moverLink);
					}
				}
				LOG.debug("  Inserted mover into new parent's existing bindings");	
			}
			else {
				bindings.add(moverLink);
				LOG.warn("  Failed to determine point of insertion - placed at end");	
			}
		}
		
		// Re-order bindings from 0, then save
		int cursor = 0;
		for (Link l : bindings) {
			if (l.equals(moverLink) || l.getOrdering() != cursor) {
				l.setOrdering(cursor);
				l.save();
			}
			cursor++;
		}
		
		// Update paths of descendant items
		String divider = newParent.isRoot() ? "" : "/";
		String newChildPath = newParent.getPath() + divider + mover.getSimpleName();
		this.itemService.updateDescendantPaths(mover.getPath(), newChildPath);
		
		// Update child item path
		this.itemService.updateItemPath(mover.getId(), newChildPath);
		
		// Force newParent links to be re-calculated, since they have now changed
		newParent.setLinks(null);
		
		// Whilst item path in db has been updated, the 'mover' object hasn't!!
		mover.setPath(newChildPath);
		
		// Update solr
		this.solrService4Cms.save(mover);
		
		// Return an instance of the moved item, even though only the path should have changed.
		// NOTE that the moved item does not express a move destination.
		return new MoverItem(this.itemService.getItemByOriginalId(mover.getOrigId()), null);
	}
	
	public Item copy(Item source, String name, String simplename) throws ResourceException {
		return copy(false, source, name, simplename);
	}
	
	private Item copy(boolean isNewVersion, Item source, String name, String simplename)
			 throws ResourceException {

		/*
		 *  The source instance will change subtly after new version is created (eg editable property),
		 *  so keep record of required data before the new version is created.
		 */
		int origVersion = source.getVersion();
		Item parent = source.getParent();
		int origOrdering = this.linkService.getLink(parent.getId(), source.getId()).getOrdering();
		long sourceId = source.getId();
		long sourceOrigId = source.getOrigId();
		
		FieldValueSet origFieldValues = source.getFieldValueSet();
		List<Link> origLinks = source.getLinks();
		String origTagStr = source.getTagsAsString();
		
		// Core data
		Item ni = CmsBeanFactory.makeItem(source.getType().getName());
		ni.assimilate(source);
		ni.
			setParent(parent).
			setDateCreated(new Timestamp(System.currentTimeMillis()));
		
		if (isNewVersion) {
			ni.
				setDeleted(false).
				setEditable(true).
				setPublished(false).
				setVersion(origVersion + 1);
		}
		else {
			ni.
				setName(name).
				setSimpleName(simplename).
				setVersion(1);
		}
		
		ni.setDateUpdated(ni.getDateCreated());
		ni = this.itemService.save(ni);
		
		/*
		 * The copy is assigned a new unique id after it is saved, and the same
		 * value is reflected in the origid field. 
		 * 
		 * The copy is also bound to the same
		 * parent item, but at the end of its list. We need to override that at 
		 * this point, so that it appears in the same position as the original was.
		 */		
		if (isNewVersion) {
			// Overwrite the 'origid' field, to be the same as the source
			ni.setOrigId(sourceOrigId);
			this.itemService.updateOrigId(ni);
		}
		
		// Tags
		saveTags(ni, origTagStr, null);
		
		// Overwrite the ordering of the parent link
		Link parentLink2NewVersion = this.linkService.getLink(parent.getId(), ni.getId());
		parentLink2NewVersion.setOrdering(origOrdering);
		parentLink2NewVersion.save();
		
		// Field data
		FieldValueSet fvs = new FieldValueSet(source.getSite());
		FieldValue nfv;
		
		for (FieldValue fv : origFieldValues.getAllValues()) {
			nfv = CmsBeanFactory.makeFieldValue();
			nfv.assimilate(fv);
			nfv.setItemId(ni.getId());
			fvs.addFieldValue(nfv);
		}
		
		ni.setFieldValues(fvs);
		saveFieldValues(ni);
		
		// Links
		List<Link> nll = new ArrayList<Link>(origLinks.size());
		Link nl;
		for (Link l : origLinks) {
			// DO NOT copy bindings, only relations, inlines and shortcuts UNLESS
			// this is a new version of an existing item.
			if (isNewVersion || ! l.getType().equals(LinkType.binding)) {
				nl = CmsBeanFactory.makeLink();
				nl.assimilate(l);
				nl.
					setParentId(ni.getId()).
					setChild(l.getChild());
				
				nll.add(nl);
			}
		}
		
		ni.setLinks(nll);		
		saveLinks(ni);
		
		// Does this item have media?
		Media m = this.mediaService.getMedia(sourceId);
		Media nm;
		if (m != null) {
			nm = CmsBeanFactory.makeMedia();
			nm.assimilate(m);
			nm.setItemId(ni.getId());
			nm.setUploadStream(m.getDownloadStream());
			this.mediaService.save(nm);
		}
		
		/* 
		 * Return the item instance with nullified field values and links,
		 * forcing these data to be re-calculated on demand.
		 */
		return ni.setLinks(null).setFieldValues(null).setTags(null);
	}
	
	public Item version(Item source) throws ResourceException {
		if (source.getType().getName().equals(ItemType.CONTENT_FOLDER_TYPE_NAME)) {
			throw new NotVersionableException(String.format("%s [%s]", "Cannot version item type", ItemType.CONTENT_FOLDER_TYPE_NAME));
		}
		else if (! source.isPublished()) {
			throw new NotVersionableException("Cannot version un-published item");
		}
		return copy(true, source, null, null);
	}
	
	public void saveFieldValues(Item i) throws ResourceException {
		if (i != null && i.getFieldValueSet() != null) {
			for (FieldValue fv : i.getFieldValueSet().getAllValues()) {
				fv.save();
			}
		}
		
		this.solrService4Cms.save(i);
	}
	
	@SuppressWarnings("unused")
	private void saveDefaultFieldValues(Item i) throws ResourceException {
		FieldValueSet fvs = i.getFieldValueSet();
		String defaultLanguage = i.getSite().getLanguage();
		String[] additionalLanguages = i.getSite().getExtraLanguagesArray();
		
		if (fvs == null || fvs.getAllValues().size() == 0) {
			fvs = new FieldValueSet(i.getSite());
			i.setFieldValues(fvs);
			
			for (FieldForType fft : this.fieldForTypeService.getFieldsForType(i.getType().getId())) {
				saveDefaultFieldValue(i, fft, defaultLanguage, fvs);
				for (String lang : additionalLanguages) {
					saveDefaultFieldValue(i, fft, lang, fvs);
				}
			}
		}
	}
	
	private void saveDefaultFieldValue(Item i, FieldForType fft, String language, FieldValueSet fvs) throws ResourceException {
		FieldValue fv = CmsBeanFactory.makeFieldValue().
			setField(fft.getField()).
			setItemId(i.getId()).
			setValue(fft.getField().getDefaultValueObject()).
			setLanguage(language);
		
		fv.save();
		fvs.addFieldValue(fv);
	}

	public void saveLinks(Item i) throws ResourceException, DuplicateItemException {
		saveLinks(i, null);
	}
	
	public List<String> saveTags(Item i, String tagStr, List<String> recentTags) {
		List<String> existingTagValues = i.getTagValues();
		List<String> latestTagValues = Arrays.asList(tagStr.split("[ ,]+"));
		List<String> freshTagValues = new ArrayList<String>();
		
		if (existingTagValues.size() != latestTagValues.size() || ! existingTagValues.containsAll(latestTagValues)) {
			this.tagService.save(i, tagStr);
			
			// Identify recently-applied tags - remove existing tags from latest, and see what's left.
			for (String v : latestTagValues) {
				if (! existingTagValues.contains(v)) {
					freshTagValues.add(v);
				}
			}
			
			// latestTagValues now contains new selections
			if (freshTagValues.size() > 0 && recentTags != null) {
				
				// Filter out duplicates
				for (String v : recentTags) {
					if (! freshTagValues.contains(v)) {
						freshTagValues.add(v);
					}
				}
			}
		}
		
		return freshTagValues;
	}
	
	public Item restoreItem(Long origId) {
		this.itemService.restoreSelectedItems(new long[] {origId});
		return this.itemService.getItemByOriginalId(origId);
	}

	public Item revert(Item i) throws ResourceException {
		if (i.getVersion() > 1) {
			this.itemService.deleteItem(i.getOrigId(), i.getVersion());
			Item r = this.itemService.getItem(i.getOrigId(), i.getVersion() - 1);
			if (r != null) {
				r.setEditable(true);
				this.itemService.updateEditable(r);
				this.solrService4Cms.save(r);
				return r;
			}
			else {
				throw new NotRevertableException(String.format("Item not found with original id %d", i.getOrigId()));				
			}
		}
		else {		
			throw new NotRevertableException("Cannot revert from existing version 1");
		}
	}

	private void saveLinks(Item i, Item dbRecord) throws ResourceException, DuplicateItemException {
		if (i.getLinks() != null) {
			if (duplicateLinks(i.getLinks())) {
				throw new DuplicateItemException("Items can only be linked once, regardless of link type or name");
			}
			
			if (dbRecord == null) {
				dbRecord = this.itemService.getItem(i.getId());
			}
			
			removeStaleLinks(dbRecord.getLinks(), i.getLinks());
			
			for (Link l : i.getLinks()) {
				l.save();
			}
		}
	}
	
	private boolean duplicateLinks(List<Link> links) {
		Link a, b;
		
		for (int i = 0; i < links.size(); i++) {
			a = links.get(i);
			for (int j = i + 1; j < links.size(); j++) {
				b = links.get(j);
				if (a.getChild().getId() == b.getChild().getId()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void removeStaleLinks(List<Link> dbRecordLinks, List<Link> updatedLinks) {
		if (dbRecordLinks != null && updatedLinks != null) {
			for (Link dbLink : dbRecordLinks) {
				if (! updatedLinks.contains(dbLink) && ! dbLink.getType().equals("binding")) {
					dbLink.delete();
					LOG.info(compose("Deleted old inline/relation link", dbLink));
				}
			}
		}
	}
	
	private String compose(String template, Object ... params) {
		return LogUtil.compose(template, params);
	}
}
