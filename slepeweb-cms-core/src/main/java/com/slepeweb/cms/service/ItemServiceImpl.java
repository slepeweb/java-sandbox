package com.slepeweb.cms.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.FieldValueSet;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.NotRevertableException;
import com.slepeweb.cms.except.NotVersionableException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.RowMapperUtil;
import com.slepeweb.commerce.bean.Product;

@Repository(value="itemService")
public class ItemServiceImpl extends BaseServiceImpl implements ItemService {
	
	private static Logger LOG = Logger.getLogger(ItemServiceImpl.class);
	private static final String MOVE_BEFORE = "before";
	private static final String MOVE_AFTER = "after";
	private static final String MOVE_OVER = "over";

	private final static String SELECT_TEMPLATE = 
			"select i.*, s.name as sitename, s.shortname as site_shortname, s.language, s.xlanguages, s.secured, " +
			"it.id as typeid, it.name as typename, it.mimetype, it.privatecache, it.publiccache, " +
			"t.id as templateid, t.name as templatename, t.forward " +
			"from item i " +
			"join site s on i.siteid = s.id " +
			"join itemtype it on i.typeid = it.id " +
			"left join template t on i.templateid=t.id " +
			"where %s";
	
	@Autowired protected LinkService linkService;
	@Autowired protected FieldValueService fieldValueService;
	@Autowired protected FieldForTypeService fieldForTypeService;
	@Autowired protected MediaService mediaService;
	@Autowired protected SolrService4Cms solrService4Cms;
	@Autowired protected CmsService cmsService;
	
	public Item save(Item i) throws ResourceException {
		return save(i, false);
	}
	
	public Item save(Item i, boolean extendedSave) throws ResourceException {
		boolean updated = false;
		
		if (! i.isDefined4Insert()) {
			throw new MissingDataException("Item data not sufficient for db insert");
		}
		
		Item dbRecord = getItem(i.getId());
		
		if (dbRecord != null) {
			update(dbRecord, i);
			updated = true;
		}
		else {
			insert(i);
		}
		
		if (extendedSave) {
			saveFieldValues(i.getFieldValueSet());
			saveLinks(i, dbRecord);
		}
		
		// Update the Solr index if item is searchable, otherwise, remove it from the index
		boolean isIndexable = i.isSearchable() && i.isPage() && i.isPublished();
		
		if (isIndexable) {
			this.solrService4Cms.save(i);
		}
		/* 
		 * We might have created a new item as a result of versioning,
		 * in which case we wouldn't want to remove any Solr documents for previous
		 * published versions.
		 */
		else if (i.getVersion() == 1){
			this.solrService4Cms.remove(i);
		}
		
		if (updated) {
			return dbRecord.setLinks(null).setFieldValues(null);
		}
		
		/* 
		 * Return the item instance with nullified field values and links,
		 * forcing these data to be re-calculated on demand.
		 */
		return i.setLinks(null).setFieldValues(null);
	}
	
	private void insert(Item i) throws ResourceException {
		// Set timestamps
		Timestamp now = new Timestamp(System.currentTimeMillis());
		i.setDateCreated(now);
		i.setDateUpdated(now);
		
		// Item table
		try {
			this.jdbcTemplate.update(
					"insert into item (name, simplename, path, siteid, typeid, templateid, datecreated, dateupdated, deleted, editable, published, searchable, version) " +
					"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					i.getName(), i.getSimpleName(), i.getPath(), i.getSite().getId(), i.getType().getId(), 
					i.getTemplate() == null ? 0 : i.getTemplate().getId(), i.getDateCreated(), i.getDateUpdated(), false, true, false, false, i.getVersion());				
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Item already exists - check the bin");
		}
		
		Long lastId = getLastInsertId();
		i.setId(lastId);
		
		// For a brand new item, the 'origid' is the same as 'id'.
		// But for a new version, the versioning code will have to overwrite this next setting:
		this.jdbcTemplate.update("update item set origid = ? where id = ?", i.getId(), i.getId());
		i.setOrigId(i.getId());
		
		/* If item has no field values, create them, with default values
		 * 
		 * Jan 2020: Different approach taken to avoid unnecessary filling up of fieldvalue table
		 * with empty values.
		 * 
		 * saveDefaultFieldValues(i); woz here
		*/ 
		LOG.info(compose("Added new item", i));
		
		// Insert binding link to parent item UNLESS we are creating/versioning the root item
		if (! i.isRoot()) {
			Item parentItem = getItem(i.getSite().getId(), i.getParentPath());				
			Item childItem = getItem(lastId);
			
			if (parentItem != null && childItem != null) {
				List<Link> existingSiblingLinks = this.linkService.getLinks(parentItem.getId());
				int ordering = existingSiblingLinks.size() + 1;
				
				Link l = CmsBeanFactory.makeLink().
					setParentId(parentItem.getId()).
					setChild(childItem).
					setType("binding").
					setName("std").
					setOrdering(ordering);
				
				this.linkService.save(l);	
			}
			else {
				String s = compose("Parent item not found", i.getParentPath());
				LOG.warn(s);
				throw new ResourceException(s);
			}			
		}
		
		if (i.getVersion() > 1) {
			retireOlderEditableVersions(i);
		}

		int maxVersions = 4;
		if (i.getVersion() > maxVersions) {
			deleteOlderVersions(i, maxVersions);
		}
	}

	private void update(Item dbRecord, Item i) {
		if (! dbRecord.equals(i)) {
			// Cannot update the simplename of a root item
			if (dbRecord.isRoot()) {
				if (dbRecord.isSiteRoot()) {
					i.setSimpleName("");
				}
				else if (dbRecord.isContentRoot()) {
					i.setSimpleName(Item.CONTENT_ROOT_PATH.substring(1));
				}
			}
			
			// simplename cannot be blank
			if (StringUtils.isBlank(i.getSimpleName())) {
				i.setSimpleName(String.valueOf(System.currentTimeMillis()));
			}
			
			boolean simplenameHasChanged = ! i.isSiteRoot() && ! dbRecord.getSimpleName().equals(i.getSimpleName());
			boolean isPublishedNow = i.isPublished() && ! dbRecord.isPublished();
			String oldPath = dbRecord.getPath();
			String newPath = i.getPath();
			
			// -Now- merge the changed properties from i into dbRecord
			dbRecord.assimilate(i);
			
			// Update timestamp
			dbRecord.setDateUpdated(new Timestamp(System.currentTimeMillis()));
			
			this.jdbcTemplate.update(
					"update item set name = ?, simplename = ?, path = ?, templateid = ?, dateupdated = ?, deleted = ?, editable = ?, published = ?, searchable = ?, version = ? where id = ?",
					dbRecord.getName(), dbRecord.getSimpleName(), dbRecord.getPath(), 
					dbRecord.getTemplate() == null ? 0 : dbRecord.getTemplate().getId(), 
					dbRecord.getDateUpdated(), dbRecord.isDeleted(), dbRecord.isEditable(), dbRecord.isPublished(), dbRecord.isSearchable(), dbRecord.getVersion(), i.getId());
			
			LOG.info(compose("Updated item", i));
			
			if (simplenameHasChanged) {
				// All child (binding) descendants will need their path properties updated
				updateDescendantPaths(oldPath, newPath);
			}
			
			if (isPublishedNow) {
				unpublishOlderVersions(dbRecord);
			}
		}
		else {
			i.setId(dbRecord.getId());
			LOG.info(compose("Item not modified", i));
		}
		
	}
	
	private void updateOrigId(Item i) {
		this.jdbcTemplate.update(
				"update item set origid = ? where id = ?",
				i.getOrigId(), i.getId());
		
		LOG.info(compose("Updated original id", i));			
	}
	
	private void updateEditable(Item i) {
		this.jdbcTemplate.update(
				"update item set editable = ? where id = ?",
				i.isEditable(), i.getId());
		
		LOG.info(compose("Updated original id", i));			
	}
	
	private void unpublishOlderVersions(Item i) {
		this.jdbcTemplate.update(
				"update item set published = 0 where siteid = ? and path = ? and version < ?",
				i.getSite().getId(), i.getPath(), i.getVersion());
		
		LOG.info(compose("Older versions now unpublished", i));
	}
	
	private void retireOlderEditableVersions(Item i) {
		int num = this.jdbcTemplate.update(
				"update item set editable = 0 where siteid = ? and path = ? and version < ?",
				i.getSite().getId(), i.getPath(), i.getVersion());
		
		LOG.info(String.format("%d older versions now uneditable", num));
	}
	
	private void deleteOlderVersions(Item i, int max) {
		this.jdbcTemplate.update(
				"delete from item where siteid = ? and path = ? and version <= ?",
				i.getSite().getId(), i.getPath(), i.getVersion() - max);
		
		LOG.info(compose("Older versions deleted", i));
	}
	
	public void saveFieldValues(FieldValueSet fieldValues) throws ResourceException {
		if (fieldValues != null) {
			for (FieldValue fv : fieldValues.getAllValues()) {
				fv.save();
			}
		}
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
	
	private void saveLinks(Item i, Item dbRecord) throws ResourceException, DuplicateItemException {
		if (i.getLinks() != null) {
			if (duplicateLinks(i.getLinks())) {
				throw new DuplicateItemException("Items can only be linked once, regardless of link type or name");
			}
			
			if (dbRecord == null) {
				dbRecord = getItem(i.getId());
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
	
	@SuppressWarnings("deprecation")
	public int getBinCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from item where deleted = 1");
	}

	@SuppressWarnings("deprecation")
	public int getVersionCount(long origid) {
		return this.jdbcTemplate.queryForInt("select count(*) from item where origid=?", origid);
	}

	public List<Item> getTrashedItems() {
		List<Item> items = this.jdbcTemplate.query(
				String.format(SELECT_TEMPLATE, "i.deleted=1 order by i.path, i.version"),
				new Object[]{}, new RowMapperUtil.ItemMapper());
		
		// Just in case any one of the trashed items is a Product ...
		for (int i = 0; i < items.size(); i++) {
			items.set(i, extendIfProduct(items.get(i)));
		}
		
		return items;
	}
	
	public int deleteTrashedItems(long[] origIds) {
		int num = 0;
		if (origIds == null) {
			for (Item i : getTrashedItems()) {
				/* 
				 * Call the delete() method of Item, instead of the deleteItem() method of this service.
				 * This ensures that if the Item is in fact a Product, then the overriden delete()
				 * method in Product gets executed.
				 */
				i.delete();
				num++;
			}
		}
		else {
			for (Long id : origIds) {
				// Above comment applies here too.
				Item i = getItemFromBin(id);
				if (i != null) {
					i.delete();
					num++;
				}
			}
		}
		
		LOG.warn(String.format("Deleted %d items from the bin", num));
		return num;
	}
	
	/*
	 * Restored items are set to 'not-published', and so shouldn't be re-indexed
	 * by Solr, but they will be once they are published manually by the user.
	 * 
	 * NOTE: This method might restore an item whose parent is still in the bin. In such a
	 * case, the cms will not display the item.
	 */
	public int restoreSelectedItems(long[] origIdArr) {
		int num = 0;
		String allItemsSql = "update item set deleted = 0, published = 0 where deleted = 1";
		String  singleItemSql = "update item set deleted = 0, published = 0 where origid = ?";
		
		if (origIdArr == null) {
			num = this.jdbcTemplate.update(allItemsSql);
			LOG.info("The entire trash bin has been restored");
		}
		else {
			for (Long origId : origIdArr) {
				num += this.jdbcTemplate.update(singleItemSql, origId);
			}
		}
		
		LOG.info(String.format("Restored %d items from the bin", num));
		return num;
	}
	
	// The 'delete' methods permanently delete items from the db that have their 'deleted' flag set.
	// The 'trash' methods perform soft-deletes, by setting/un-setting the 'deleted' flag.
	
	public int trashItemAndDirectChildren(Item i) {
		// Delete all versions of this item
		int count = this.jdbcTemplate.update("update item set deleted = 1 where origid = ?", i.getOrigId());
		if (count > 0) {
			LOG.info(compose("Trashed item", String.valueOf(i)));
			
			// Remove item from Solr index
			this.solrService4Cms.remove(i);
			
			// Now attend to any child items
			List<Link> list = this.linkService.getBindings(i.getId());
				
			for (Link l : list) {
				count += trashItemAndDirectChildren(l.getChild());
			}
		}
		
		return count;
	}
	
	public Item restoreItem(Long origId) {
		restoreSelectedItems(new long[] {origId});
		return getItemByOriginalId(origId);
	}

	public Item revert(Item i) throws ResourceException {
		if (i.getVersion() > 1) {
			deleteItem(i.getOrigId(), i.getVersion());
			Item r = getItem(i.getOrigId(), i.getVersion() - 1);
			if (r != null) {
				r.setEditable(true);
				updateEditable(r);
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

	public void deleteAllVersions(Long origId) {
		if (this.jdbcTemplate.update("delete from item where origid = ?", origId) > 0) {
			LOG.warn(compose("Deleted item and all its versions", String.valueOf(origId)));
		}
	}

	// This deletes a specific version of an item - required for 'revert' functionality.
	public void deleteItem(Long origId, int version) {
		if (this.jdbcTemplate.update("delete from item where origid = ? and version = ?", origId, version) > 0) {
			LOG.warn(compose("Deleted item version", String.valueOf(origId), String.valueOf(version)));
		}
	}

	public Item getItem(Long siteId, String path) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.siteid=? and i.path=? and i.deleted=0" + getVersionClause()),
			new Object[]{siteId, path});
	}

	public Item getEditableVersion(Long siteId, String path) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.siteid=? and i.path=? and i.deleted=0 and i.editable=1"),
			new Object[]{siteId, path});
	}

	/*
	 * Intended for use where image may have a series of images related by name, and not
	 * using the Link table.
	 * TODO: Review whether this is going to be useful.
	 */
	public List<Item> getItemsByPathLike(Long siteId, String path) {
		String sql = String.format(SELECT_TEMPLATE, "i.siteid=? and i.path like ? and i.deleted=0" + getVersionClause());
		List<Item> list = this.jdbcTemplate.query(
				sql, new Object[]{siteId, path + "%"}, new RowMapperUtil.ItemMapper());
		
		for (int i = 0; i < list.size(); i++) {
			list.set(i, extendIfProduct(list.get(i)));
		}
		
		return list;
	}

	public Item getItem(Long id) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.id=? and i.deleted=0"), 
			new Object[]{id});
	}
	
	public Item getItemByOriginalId(Long origId) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.origid=? and i.deleted=0" + getVersionClause()), 
			new Object[]{origId});
	}
	
	public Item getItem(Long origId, int version) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.origid=? and version=? and i.deleted=0"), 
			new Object[]{origId, version});
	}
	
	public Item getEditableVersion(Long origId) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.origid=? and i.deleted=0 and i.editable=1"), 
			new Object[]{origId});
	}
	
	public Item getPublishedVersion(Long origId) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.origid=? and i.deleted=0 and i.published=1"), 
			new Object[]{origId});
	}
	
	public List<Item> getAllVersions(Long origId) {
		return this.jdbcTemplate.query(
			String.format(SELECT_TEMPLATE, "i.origid=? and i.deleted=0 order by i.version"),
			new Object[]{origId}, new RowMapperUtil.ItemMapper());		
	}

	public Item getItemFromBin(Long origId) {
		// This operation isn't really interested in the language property
		return getItem(
			String.format(SELECT_TEMPLATE, "i.origid=? and i.editable=1 and i.deleted=1"), 
			new Object[]{origId});
	}
	
	@Deprecated
	public int getCount() {
		return getCount(null);
	}
	
	@Deprecated
	public int getCount(String path) {
		if (StringUtils.isNotBlank(path)) {
			return this.jdbcTemplate.queryForInt("select count(*) from item where path like ?", path + "%");
		}
		else {
			return this.jdbcTemplate.queryForInt("select count(*) from item");
		}
	}
	
	@SuppressWarnings("deprecation")
	public int getCountByType(Long itemTypeId) {
		return this.jdbcTemplate.queryForInt("select count(*) from item where typeid = ?", itemTypeId);
	}
	
	public boolean move(Item mover, Item currentParent, Item targetParent, Item target) throws ResourceException {
		
		return move(mover, currentParent, targetParent, target, "over");
	}
	
	/*
	 * This provides a relative move, ie before/after target.
	 * If mode == "over", then target is effectively a new parent.
	 */
	public boolean move(Item mover, Item currentParent, Item targetParent, Item target, 
			String mode) throws ResourceException {
		
		if (mover == null || target == null || currentParent == null || mode == null) {
			throw new ResourceException("Missing item data for move");
		}
		
		LOG.debug(String.format("Moving [%s] (mover) %s [%s] (target)", mover, mode.toUpperCase(), target));			
		LOG.debug(compose("  Old parent", currentParent));		
		Item newParent = mode.equals(MOVE_OVER) ? target : targetParent;
		LOG.debug(compose("  New parent", newParent));		
		
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
		this.linkService.deleteLinks(currentParent.getId(), mover.getId());
		LOG.debug("  Removed links between mover and old parent");		
		
		// Bind to new parent - we'll save() the mover link later
		Link moverLink = CmsBeanFactory.makeLink().
				setParentId(newParent.getId()).
				setChild(mover).
				setType(LinkType.binding).
				setName("std");
		
		// Add mover to new parent's bindings
		List<Link> bindings = newParent.getBindings();
		
		if (mode.equals(MOVE_OVER)) {
			bindings.add(moverLink);
			LOG.debug("  Added mover to end of new parent's existing bindings");	
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
				if (mode.equals(MOVE_BEFORE)) {
					bindings.add(cursor, moverLink);
				}
				else if (mode.equals(MOVE_AFTER)) {
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
		updateDescendantPaths(mover.getPath(), newChildPath);
		
		// Update child item path
		updateItemPath(mover.getId(), newChildPath);
		
		// Force newParent links to be re-calculated, since they have now changed
		newParent.setLinks(null);
		
		return true;
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
		ni = save(ni);
		
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
			updateOrigId(ni);
		}
		
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
		
		ni = save(ni, true);
		
		// Does this item have media?
		Media m = this.mediaService.getMedia(sourceId);
		Media nm;
		if (m != null) {
			nm = CmsBeanFactory.makeMedia();
			nm.assimilate(m);
			nm.setItemId(ni.getId());
			try {
				nm.setInputStream(m.getBlob().getBinaryStream());
			}
			catch (SQLException e) {
				LOG.error("Failed to copy media", e);
			}
			this.mediaService.save(nm);
		}
		
		/* 
		 * Return the item instance with nullified field values and links,
		 * forcing these data to be re-calculated on demand.
		 */
		return ni.setLinks(null).setFieldValues(null);
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
	
	@SuppressWarnings("deprecation")
	public int getCountByPath(Item i) {
		return this.jdbcTemplate.queryForInt(
				"select count(*) from item where siteid=? and path like ?", new Object[] {i.getSite().getId(), i.getPath() + "%"});
	}
	
	public boolean updatePublished(Long id, boolean option) {
		return updateBinaryProperty("published", id, option);
	}
	
	public boolean updateSearchable(Long id, boolean option) {
		return updateBinaryProperty("searchable", id, option);
	}
	
	private boolean updateBinaryProperty(String column, Long id, boolean option) {
		int flag = option ? 1 : 0;
		String sql = String.format("update item set %s = ? where id = ?", column);
		
		return this.jdbcTemplate.update(sql, flag, id) > 0;
	}
	
	private Item getItem(String sql, Object[] params) {
		Item i = (Item) getLastInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.ItemMapper()));
		
		return extendIfProduct(i);
	}

	private Item extendIfProduct(Item i) {
		if (i != null && i.isProduct() && i.getOrigId() != null) {
			Product p = (Product) i;
			Product dbRecord = this.cmsService.getProductService().get(i.getOrigId());
			if (dbRecord != null) {
				p.assimilateProduct(dbRecord);
				return p;
			}
		}
		return i;
	}
	
	/*
	 * Ensure that both string args have trailing slash.
	 * TODO: This approach fails if (for some reason like coding error) an descendant item's path
	 *       has become corrupted - it will get ignored.
	 */
	private void updateDescendantPaths(String oldPath, String newPath) {
		if (! oldPath.endsWith("/")) oldPath += "/";
		if (! newPath.endsWith("/")) newPath += "/";
		this.jdbcTemplate.update("update item set path = replace(path, ?, ?) where path like ?", oldPath, newPath, oldPath + "%");
	}
	
	private void updateItemPath(Long itemId, String newPath) {
		this.jdbcTemplate.update("update item set path = ? where id = ?", newPath, itemId);
	}
}
