package com.slepeweb.cms.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.RowMapperUtil;
import com.slepeweb.commerce.bean.Product;

@Repository(value="itemService")
public class ItemServiceImpl extends BaseServiceImpl implements ItemService {
	
	private static Logger LOG = Logger.getLogger(ItemServiceImpl.class);

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
	@Autowired protected MediaFileService mediaFileService;
	@Autowired protected SolrService4Cms solrService4Cms;
	@Autowired protected CmsService cmsService;
	
	public Item save(Item i) throws ResourceException {
		if (! i.isDefined4Insert()) {
			throw new MissingDataException("Item data not sufficient for db insert");
		}
		
		Item dbRecord = getItem(i.getId());
		
		if (dbRecord != null) {
			String oldSimpleName = dbRecord.getSimpleName();
			update(dbRecord, i);
			
			if (dbRecord.getSimpleName().equals(oldSimpleName)) {
				this.solrService4Cms.save(dbRecord);
			}
			else {
				this.solrService4Cms.indexSection(dbRecord);
			}
			
			/* 
			 * Return the updated item instance with nullified field values, links and tags,
			 * forcing these data to be re-calculated on demand.
			 */
			return dbRecord.setLinks(null).setFieldValues(null).setTags(null);
		}
		else {
			insert(i);
			this.solrService4Cms.save(i);
			
			/* 
			 * Return the inserted item instance with nullified field values, links and tags,
			 * forcing these data to be re-calculated on demand.
			 */
			return i.setLinks(null).setFieldValues(null).setTags(null);
		}
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
	
	public void updateOrigId(Item i) {
		this.jdbcTemplate.update(
				"update item set origid = ? where id = ?",
				i.getOrigId(), i.getId());
		
		LOG.info(compose("Updated original id", i));			
	}
	
	public void updateEditable(Item i) {
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
	
	// TODO: This is flawed, because item path could change between versions.
	// TODO: Also, any associated media files on the file system should be deleted. In
	//       the meanwhile, note that redundant media files for old versions will
	//       remain in the file system.
	private void deleteOlderVersions(Item i, int max) {
		this.jdbcTemplate.update(
				"delete from item where siteid = ? and path = ? and version <= ?",
				i.getSite().getId(), i.getPath(), i.getVersion() - max);
		
		LOG.info(compose("Older versions deleted", i));
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
				// First of all delete associated files from the file store. This MUST be the first step.
				// (Remember that media and item tables are linked by a foreign key constraint 'on delete cascade')
				if (i.getType().isMedia()) {
					this.mediaFileService.delete(i);
				}
				
				
				/* 
				 * THEN call the delete() method of Item, instead of the deleteItem() method of this service.
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
					this.mediaFileService.delete(i);
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
	// This method is recursive - it also trashes ALL descendant items
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
	public int getCount(long siteId) {
		return getCountByPath(siteId, null);
	}
	
	@SuppressWarnings("deprecation")
	public int getCountByType(Long itemTypeId) {
		return this.jdbcTemplate.queryForInt("select count(*) from item where typeid = ?", itemTypeId);
	}
	
	@SuppressWarnings("deprecation")
	public int getCountByPath(long siteId, String path) {
		return this.jdbcTemplate.queryForInt(
				"select count(*) from item where siteid=? and path like ? and deleted=0", new Object[] {siteId, path + "%"});
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
	public void updateDescendantPaths(String oldPath, String newPath) {
		if (! oldPath.endsWith("/")) oldPath += "/";
		if (! newPath.endsWith("/")) newPath += "/";
		this.jdbcTemplate.update("update item set path = replace(path, ?, ?) where path like ?", oldPath, newPath, oldPath + "%");
	}
	
	public void updateItemPath(Long itemId, String newPath) {
		this.jdbcTemplate.update("update item set path = ? where id = ?", newPath, itemId);
	}
}
