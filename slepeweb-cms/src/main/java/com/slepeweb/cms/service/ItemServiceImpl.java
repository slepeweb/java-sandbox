package com.slepeweb.cms.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.Link.LinkType;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class ItemServiceImpl extends BaseServiceImpl implements ItemService {
	
	private static Logger LOG = Logger.getLogger(ItemServiceImpl.class);
	private static final String SELECT_TEMPLATE = 
			"select i.*, s.name as sitename, s.hostname, " +
			"it.id as typeid, it.name as typename, it.media from " +
			"item i, site s, itemtype it where " +
			"i.siteid=s.id and i.typeid=it.id and %s and i.deleted=0";
	
	@Autowired protected LinkService linkService;
	@Autowired protected FieldValueService fieldValueService;
	@Autowired protected FieldForTypeService fieldForTypeService;
	@Autowired protected MediaService mediaService;

	private String columns = "name, simplename, path, siteid, typeid, datecreated, dateupdated, deleted";
	
	public Item save(Item i) {
		if (i.isDefined4Insert()) {
			Item dbRecord = getItem(i.getId());		
			if (dbRecord != null) {
				updateItem(dbRecord, i);
			}
			else {
				insertItem(i);
			}
			
			saveFieldValues(i);
			removeStaleLinks(dbRecord, i);
			saveCurrentLinks(i);
			saveMedia(i);
		}
		
		return i;
	}
	
	private void insertItem(Item i) {
		// Item table
		this.jdbcTemplate.update(
				String.format("insert into item (%s) values (%s)", columns, placeholders4Insert(columns)),
				i.getName(), i.getSimpleName(), i.getPath(), i.getSite().getId(), i.getType().getId(), 
				i.getDateCreated(), i.getDateUpdated(), false);				
		
		Long lastId = getLastInsertId();
		i.setId(lastId);
		setDefaultFieldValues(i);
		LOG.info(compose("Added new item", i));
		
		// Insert binding link to parent item
		if (! i.isRoot()) {
			Item parentItem = getItem(i.getSite().getId(), i.getParentPath());				
			Item childItem = getItem(lastId);
			
			if (parentItem != null && childItem != null) {
				List<Link> existingSiblingLinks = this.linkService.getLinks(parentItem.getId());
				int ordering = existingSiblingLinks.size() + 1;
				
				Link l = CmsBeanFactory.getLink().
					setParentId(parentItem.getId()).
					setChild(childItem).
					setType(LinkType.binding).
					setName("std").
					setOrdering(ordering);
				
				this.linkService.save(l);	
			}
			else {
				LOG.warn(compose("Parent item not found", i.getParentPath()));
			}
		}
	}

	private void updateItem(Item dbRecord, Item i) {
		if (! dbRecord.equals(i)) {
			boolean simplenameHasChanged = ! dbRecord.getSimpleName().equals(i.getSimpleName());
			String oldPath = dbRecord.getPath();
			String newPath = i.getPath();
			
			// -Now- merge the changed properties from i into dbRecord
			dbRecord.assimilate(i);
			
			this.jdbcTemplate.update(
					String.format("update item set %s where id = ?", placeholders4Update(this.columns)),
					dbRecord.getName(), dbRecord.getSimpleName(), dbRecord.getPath(), 
					dbRecord.getSite().getId(), dbRecord.getType().getId(), 
					dbRecord.getDateCreated(), dbRecord.getDateUpdated(), dbRecord.isDeleted(), i.getId());
			
			LOG.info(compose("Updated item", i));
			
			if (simplenameHasChanged) {
				// All child (binding) descendants will need their path properties updated
				updateDescendantPaths(oldPath, newPath);
			}
		}
		else {
			i.setId(dbRecord.getId());
			LOG.info(compose("Item not modified", i));
		}
		
	}
	
	private void saveFieldValues(Item i) {
		if (i.getFieldValues() != null) {
			for (FieldValue fv : i.getFieldValues()) {
				fv.save();
			}
		}
	}
	
	private void saveMedia(Item i) {
		if (i.getMediaUploadFilePath() != null && i.getType().isMedia()) {
			this.mediaService.save(i);
		}
	}
	
	private void setDefaultFieldValues(Item i) {
		// If item has no field values, create them, with default values
		if (i.getFieldValues() == null || i.getFieldValues().size() == 0) {
			i.setFieldValues(new ArrayList<FieldValue>());
			FieldValue fv;

			for (FieldForType fft : this.fieldForTypeService.getFieldsForType(i.getType().getId())) {
				fv = CmsBeanFactory.getFieldValue().
					setField(fft.getField()).
					setItemId(i.getId()).
					setValue(fft.getField().getDefaultValue()).
					setDateUpdated(new Timestamp(System.currentTimeMillis()));
				
				i.getFieldValues().add(fv);
			}
		}
	}

	private void saveCurrentLinks(Item i) {
		if (i.getLinks() != null) {
			for (Link l : i.getLinks()) {
				l.save();
			}
		}
	}
	
	private void removeStaleLinks(Item dbRecord, Item i) {
		if (dbRecord != null && dbRecord.getLinks() != null && i.getLinks() != null) {
			for (Link dbLink : dbRecord.getLinks()) {
				if (! i.getLinks().contains(dbLink)) {
					dbLink.delete();
					LOG.info(compose("Deleted old child link", dbLink));
				}
			}
		}
	}

	public void deleteItem(Long id) {
		if (this.jdbcTemplate.update("delete from item where id = ?", id) > 0) {
			LOG.warn(compose("Deleted item", String.valueOf(id)));
			
			// TODO: Should move descendants to an orphan bin, perhaps retaining original paths ...
		}
	}

	public void deleteItem(Item i) {
		deleteItem(i.getId());
	}

	public Item getItem(Long siteId, String path) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.siteid=? and i.path=?"),
			new Object[]{siteId, path});
	}

	public Item getItem(Long id) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.id=?"), 
			new Object[]{id});
	}
	
	public int getCount() {
		return getCount(null);
	}
	
	public int getCount(String path) {
		if (StringUtils.isNotBlank(path)) {
			return this.jdbcTemplate.queryForInt("select count(*) from item where path like ?", path + "%");
		}
		else {
			return this.jdbcTemplate.queryForInt("select count(*) from item");
		}
	}
	
	public int getCountByType(Long itemTypeId) {
		return this.jdbcTemplate.queryForInt("select count(*) from item where typeid = ?", itemTypeId);
	}
	
	/*
	 * This method can be used if a child item moves to a different parent, and  
	 * NOT if the child's simplename has changed.
	 */
	public void move(Item child, Item newParent) {
		Link parentLink = this.linkService.getParent(child.getId());
		if (parentLink != null) {
			// Un-link from current path
			this.linkService.deleteLinks(parentLink.getParentId(), child.getId());
			
			// Bind to new parent
			parentLink.setParentId(newParent.getId()).setOrdering(1);
			parentLink.save();
			
			// Update paths of descendant items
			String divider = newParent.isRoot() ? "" : "/";
			String newChildPath = newParent.getPath() + divider + child.getSimpleName();
			updateDescendantPaths(child.getPath(), newChildPath);
			
			// Update child item path
			updateItemPath(child.getId(), newChildPath);
			
			// Force newParent links to be re-calculated, since they have now changed
			newParent.setLinks(null);
		}
		else {
			LOG.error(compose("Failed to identify parent item", child.getPath()));
		}
	}

	private Item getItem(String sql, Object[] params) {
		return (Item) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.ItemMapper()));
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
