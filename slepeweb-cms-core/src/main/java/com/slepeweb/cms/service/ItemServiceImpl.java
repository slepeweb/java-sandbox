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
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.component.ServerConfig;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.NotRevertableException;
import com.slepeweb.cms.except.NotVersionableException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.RowMapperUtil;
import com.slepeweb.commerce.bean.Product;

// TODO: Can getItem() return a Product (if item type is Product)?

@Repository(value="itemService")
public class ItemServiceImpl extends BaseServiceImpl implements ItemService {
	
	private static Logger LOG = Logger.getLogger(ItemServiceImpl.class);
	private static final String MOVE_BEFORE = "before";
	private static final String MOVE_AFTER = "after";
	private static final String MOVE_OVER = "over";

	private final static String SELECT_TEMPLATE = 
			"select i.*, s.name as sitename, s.shortname as site_shortname, " +
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
	@Autowired protected SolrService solrService;
	@Autowired protected CmsService cmsService;
	@Autowired protected ServerConfig config;
	
	public Item save(Item i) throws MissingDataException, DuplicateItemException {
		return save(i, false);
	}
	
	public Item save(Item i, boolean extendedSave) throws MissingDataException, DuplicateItemException {
		
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
			saveFieldValues(i.getFieldValues());
			saveLinks(i, dbRecord);
		}
		
		// Update the Solr index if item is searchable, otherwise, remove it from the index
		boolean isIndexable = i.isSearchable() && i.isPage() && i.isPublished();
		
		if (isIndexable) {
			this.solrService.save(i);
		}
		/* 
		 * We might have created a new item as a result of versioning,
		 * in which case we wouldn't want to remove any Solr documents for previous
		 * published versions.
		 */
		else if (i.getVersion() == 1){
			this.solrService.remove(i);
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
	
	private void insert(Item i) throws MissingDataException, DuplicateItemException {
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
		
		saveDefaultFieldValues(i);
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
				LOG.warn(compose("Parent item not found", i.getParentPath()));
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
			boolean simplenameHasChanged = ! dbRecord.getSimpleName().equals(i.getSimpleName());
			boolean isPublishedNow = i.isPublished() && ! dbRecord.isPublished();
			String oldPath = dbRecord.getPath();
			String newPath = i.getPath();
			
			// -Now- merge the changed properties from i into dbRecord
			dbRecord.assimilate(i);
			
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
	
	public void saveFieldValues(List<FieldValue> fieldValues) throws MissingDataException {
		if (fieldValues != null) {
			for (FieldValue fv : fieldValues) {
				fv.save();
			}
		}
	}
	
	private void saveDefaultFieldValues(Item i) throws MissingDataException {
		// If item has no field values, create them, with default values
		if (i.getFieldValues() == null || i.getFieldValues().size() == 0) {
			i.setFieldValues(new ArrayList<FieldValue>());
			FieldValue fv;

			for (FieldForType fft : this.fieldForTypeService.getFieldsForType(i.getType().getId())) {
				fv = CmsBeanFactory.makeFieldValue().
					setField(fft.getField()).
					setItemId(i.getId()).
					setValue(fft.getField().getDefaultValueObject());
				
				fv.save();
				i.getFieldValues().add(fv);
			}
		}
	}

	public void saveLinks(Item i) throws MissingDataException {
		saveLinks(i, null);
	}
	
	private void saveLinks(Item i, Item dbRecord) throws MissingDataException {
		if (i.getLinks() != null) {
			if (dbRecord == null) {
				dbRecord = getItem(i.getId());
			}
			
			removeStaleLinks(dbRecord.getLinks(), i.getLinks());
			
			for (Link l : i.getLinks()) {
				l.save();
			}
		}
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
				Item i = getItemByOriginalId(id);
				i.delete();
			}
			num = origIds.length;
		}
		
		LOG.warn(String.format("Deleted %d items from the bin", num));
		return num;
	}
	
	/*
	 * Restored items are set to 'not-published', and so shouldn't be re-indexed
	 * by Solr, but they will be once they are published manually by the user.
	 */
	public int restoreSelectedItems(long[] idArr) {
		int num = 0;
		String allItemsSql = "update item set deleted = 0, published = 0 where deleted = 1";
		String  singleItemSql = "update item set deleted = 0, published = 0 where origid = ?";
		
		if (idArr == null) {
			if ((num = this.jdbcTemplate.update(allItemsSql)) > 0) {
				LOG.info("The entire trash bin has been restored");
			}
		}
		else {
			int c;
			Item i;
			for (Long id : idArr) {
				i = getItemFromBin(id);
				if (i != null && ((c = this.jdbcTemplate.update(singleItemSql, i.getOrigId()))) > 0) {
					num += c;
				}
			}
			LOG.info(String.format("Restored %d items from the bin", num));
		}
		return num;
	}
	
	// The 'delete' methods permanently delete items from the db that have their 'deleted' flag set.
	// The 'trash' methods perform soft-deletes, by setting/un-setting the 'deleted' flag.
	
	public Item trashItem(Long id) {
		Item i = getItem(id);

		// Delete all versions of this item
		if (this.jdbcTemplate.update("update item set deleted = 1 where origid = ?", i.getOrigId()) > 0) {
			LOG.info(compose("Trashed item", String.valueOf(i)));
			
			// Remove item from Solr index
			this.solrService.remove(i);
			
			// Now attend to any child items
			List<Link> list = this.linkService.getBindings(i.getId());
				
			for (Link l : list) {
				trashItem(l.getChild().getId());
			}
		}
		
		return getItem(id);
	}
	
	public Item restoreItem(Long id) {
		restoreSelectedItems(new long[] {id});
		return getItem(id);
	}

	public Item revert(Item i) throws NotRevertableException {
		if (i.getVersion() > 1) {
			deleteItem(i.getOrigId(), i.getVersion());
			Item r = getItem(i.getOrigId(), i.getVersion() - 1);
			if (r != null) {
				r.setEditable(true);
				updateEditable(r);
				this.solrService.save(r);
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

	public Item getItem(Long id) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.id=? and i.deleted=0"), 
			new Object[]{id});
	}
	
	public Item getItemByOriginalId(Long id) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.origid=? and i.deleted=0" + getVersionClause()), 
			new Object[]{id});
	}
	
	public Item getItem(Long origId, int version) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.origid=? and version=? and i.deleted=0"), 
			new Object[]{origId, version});
	}
	
	@SuppressWarnings("unused")
	private List<Item> getAllVersions(Long origId) {
		return this.jdbcTemplate.query(
			String.format(SELECT_TEMPLATE, "i.origid=? and i.deleted=0 order by i.version"),
			new Object[]{origId}, new RowMapperUtil.ItemMapper());		
	}

	public Item getItemFromBin(Long id) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.id=? and i.deleted=1"), 
			new Object[]{id});
	}
	
	public int getCount() {
		return getCount(null);
	}
	
	@SuppressWarnings("deprecation")
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
	
	public boolean move(Item mover, Item currentParent, Item newParent, boolean shortcut) 
			throws MissingDataException, ResourceException {
		
		return move(mover, currentParent, newParent, shortcut, "over");
	}
	
	/*
	 * This provides a relative move, ie before/after target.
	 * If mode == "over", then target is effectively a new parent.
	 */
	public boolean move(Item mover, Item currentParent, Item target, boolean isShortcut, String mode) 
			throws MissingDataException, ResourceException {
		
		if (mover == null || target == null || currentParent == null || mode == null) {
			throw new ResourceException("Missing item data for move");
		}
		
		LOG.debug(String.format("Moving [%s] (mover) %s [%s] (target)", mover, mode.toUpperCase(), target));			
		LOG.debug(compose("  Old parent", currentParent));		
		Item newParent = mode.equals(MOVE_OVER) ? target : target.getParent();
		LOG.debug(compose("  New parent", newParent));		
		
		// Break the parent link for the mover, EVEN IF old-parent = new-parent
		this.linkService.deleteLinks(currentParent.getId(), mover.getId());
		LOG.debug("  Removed links between mover and old parent");		
		
		// Bind to new parent - we'll save() the mover link later
		Link moverLink = CmsBeanFactory.makeLink().
				setParentId(newParent.getId()).
				setChild(mover).
				setType(isShortcut ? LinkType.shortcut : LinkType.binding).
				setName("std");
		
		// Add mover to new parent's bindings
		List<Link> bindings = this.linkService.getBindings(newParent.getId());
		
		if (mode.equals(MOVE_OVER)) {
			bindings.add(moverLink);
			LOG.debug("  Added mover to end of new parent's existing bindings");	
		}
		else {
			// If mode is 'before' or 'after', identify insertions point and re-order all siblings
			int cursor = -1;
			for (Link l : bindings) {
				if (l.getChild().getId().longValue() == target.getId().longValue()) {
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
		
		// Update paths of descendant items, but NOT for shortcuts
		if (! isShortcut) {
			String divider = newParent.isRoot() ? "" : "/";
			String newChildPath = newParent.getPath() + divider + mover.getSimpleName();
			updateDescendantPaths(mover.getPath(), newChildPath);
			
			// Update child item path
			updateItemPath(mover.getId(), newChildPath);
		}
		
		// Force newParent links to be re-calculated, since they have now changed
		newParent.setLinks(null);
		
		return true;
	}
	
	public Item copy(Item source, String name, String simplename) 
			throws MissingDataException, DuplicateItemException {
		
		return copy(false, source, name, simplename);
	}
	
	private Item copy(boolean isNewVersion, Item source, String name, String simplename)
			 throws MissingDataException, DuplicateItemException {

		/*
		 *  The source instance will change subtly after new version is created (eg editable property),
		 *  so keep record of required data before the new version is created.
		 */
		int origVersion = source.getVersion();
		Item parent = source.getParent();
		int origOrdering = this.linkService.getLink(parent.getId(), source.getId()).getOrdering();
		long sourceId = source.getId();
		long sourceOrigId = source.getOrigId();
		List <FieldValue> origFieldValues = source.getFieldValues();
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
		List <FieldValue> nfvl = new ArrayList<FieldValue>(origFieldValues.size());
		FieldValue nfv;
		for (FieldValue fv : origFieldValues) {
			nfv = CmsBeanFactory.makeFieldValue();
			nfv.assimilate(fv);
			nfv.setItemId(ni.getId());
			nfvl.add(nfv);
		}
		ni.setFieldValues(nfvl);
		
		// Links
		List<Link> nll = new ArrayList<Link>(origLinks.size());
		Link nl;
		for (Link l : origLinks) {
			nl = CmsBeanFactory.makeLink();
			nl.assimilate(l);
			nl.
				setParentId(ni.getId()).
				setChild(l.getChild());
			nll.add(nl);
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
	
	public Item version(Item source) throws NotVersionableException, MissingDataException, DuplicateItemException {
		if (source.getType().getName().equals(ItemType.CONTENT_FOLDER_TYPE_NAME)) {
			throw new NotVersionableException(String.format("%s [%s]", "Cannot version item type", ItemType.CONTENT_FOLDER_TYPE_NAME));
		}
		else if (! source.isPublished()) {
			throw new NotVersionableException("Cannot version un-published item");
		}
		return copy(true, source, null, null);
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
				p.assimilate(dbRecord);
				return p;
			}
		}
		return i;
	}
	
	/*
	 * Ensure that both string args have trailing slash
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
