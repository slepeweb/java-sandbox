package com.slepeweb.cms.service;

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
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class ItemServiceImpl extends BaseServiceImpl implements ItemService {
	
	private static Logger LOG = Logger.getLogger(ItemServiceImpl.class);
	private static final String MOVE_BEFORE = "before";
	private static final String MOVE_AFTER = "after";
	private static final String MOVE_OVER = "over";

	private final static String SELECT_TEMPLATE = 
			"select i.*, s.name as sitename, s.hostname, s.shortname as site_shortname, it.id as typeid, it.name as typename, it.mimetype, " +
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

	public Item save(Item i) {
		return save(i, false);
	}
	
	public Item save(Item i, boolean extendedSave) {
		boolean updated = false;
		
		if (i.isDefined4Insert()) {
			Item dbRecord = getItem(i.getId());		
			if (dbRecord != null) {
				updateItem(dbRecord, i);
				updated = true;
			}
			else {
				insertItem(i);
			}
			
			if (extendedSave) {
				saveFieldValues(i.getFieldValues());
				saveLinks(i, dbRecord);
//				saveMedia(i);
			}
			
			if (updated) {
				return dbRecord;
			}
		}
		else {
			LOG.error(compose("Item not saved - insufficient data", i));
		}
		
		return i;
	}
	
	private void insertItem(Item i) {
		// Item table
		this.jdbcTemplate.update(
				"insert into item (name, simplename, path, siteid, typeid, templateid, datecreated, dateupdated, deleted, published) " +
				"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				i.getName(), i.getSimpleName(), i.getPath(), i.getSite().getId(), i.getType().getId(), 
				i.getTemplate() == null ? 0 : i.getTemplate().getId(), i.getDateCreated(), i.getDateUpdated(), false, false);				
		
		Long lastId = getLastInsertId();
		i.setId(lastId);
		saveDefaultFieldValues(i);
		LOG.info(compose("Added new item", i));
		
		// Insert binding link to parent item
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
	}

	private void updateItem(Item dbRecord, Item i) {
		if (! dbRecord.equals(i)) {
			boolean simplenameHasChanged = ! dbRecord.getSimpleName().equals(i.getSimpleName());
			String oldPath = dbRecord.getPath();
			String newPath = i.getPath();
			
			// -Now- merge the changed properties from i into dbRecord
			dbRecord.assimilate(i);
			
			this.jdbcTemplate.update(
					"update item set name = ?, simplename = ?, path = ?, templateid = ?, dateupdated = ?, deleted = ?, published = ? where id = ?",
					dbRecord.getName(), dbRecord.getSimpleName(), dbRecord.getPath(), 
					dbRecord.getTemplate() == null ? 0 : dbRecord.getTemplate().getId(), 
					dbRecord.getDateUpdated(), dbRecord.isDeleted(), dbRecord.isPublished(), i.getId());
			
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
	
	public void saveFieldValues(List<FieldValue> fieldValues) {
		if (fieldValues != null) {
			for (FieldValue fv : fieldValues) {
				fv.save();
			}
		}
	}
	
//	private void saveMedia(Item i) {
//		if (i.getMediaUploadFilePath() != null && i.getType().isMedia()) {
//			this.mediaService.save(i);
//		}
//	}
	
	private void saveDefaultFieldValues(Item i) {
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

	public void saveLinks(Item i) {
		saveLinks(i, null);
	}
	
	private void saveLinks(Item i, Item dbRecord) {
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
	
	public int getBinCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from item where deleted = 1");
	}

	private Item trashAction(Long id, String actionHeading, int actionCode) {
		Item i = actionCode == 1 ? getItem(id) : getItemFromBin(id);
		
		// Action the given item
		if (this.jdbcTemplate.update("update item set deleted = ? where id = ?", actionCode, id) > 0) {
			LOG.warn(compose(String.format("%sed item", actionHeading), String.valueOf(id)));
			
			// Now action any descendant items
			int count = this.jdbcTemplate.update("update item set deleted = ? where siteid = ? and path like ?", 
					actionCode, i.getSite().getId(), i.getPath() + "/%");
			
			LOG.warn(compose(String.format("%sed %d descendant items", actionHeading, count), String.valueOf(i.getPath())));
		}
		
		return i.setDeleted(actionCode == 1);
	}

	public Item trashItem(Long id) {
		return trashAction(id, "Trash", 1);
	}
	
	public Item restoreItem(Long id) {
		return trashAction(id, "Restore", 0);
	}

	public void deleteItem(Long id) {
		if (this.jdbcTemplate.update("delete from item where id = ? and deleted = 1", id) > 0) {
			LOG.warn(compose("Deleted item", String.valueOf(id)));
		}
	}

	public void deleteItem(Item i) {
		deleteItem(i.getId());
	}

	public Item getItem(Long siteId, String path) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.siteid=? and i.path=? and i.deleted=0" + getPublishedClause()),
			new Object[]{siteId, path});
	}

	public Item getItem(Long id) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.id=? and i.deleted=0" + getPublishedClause()), 
			new Object[]{id});
	}
	
	public Item getItemFromBin(Long id) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.id=? and i.deleted=1" + getPublishedClause()), 
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
	
	public boolean move(Item mover, Item parent) {
		return move(mover, parent, "over");
	}
	
	/*
	 * This provides a relative move, ie before/after target.
	 * If mode == "over", then target is effectively a new parent.
	 */
	public boolean move(Item mover, Item target, String mode) {
		
		LOG.debug(String.format("Moving [%s] (mover) %s [%s] (target)", mover, mode.toUpperCase(), target));		
		Item oldParent = mover.getParent();		
		
		if (oldParent != null) {
			LOG.debug(compose("Old parent", oldParent));		
			Item newParent = mode.equals(MOVE_OVER) ? target : target.getParent();
			LOG.debug(compose("New parent", newParent));		
			
			// Break the parent link for the mover, EVEN IF old-parent = new-parent
			this.linkService.deleteLinks(oldParent.getId(), mover.getId());
			LOG.debug("Removed links between mover and old parent");		
			
			// Bind to new parent - we'll save() the mover link later
			Link moverLink = CmsBeanFactory.makeLink().
					setParentId(newParent.getId()).
					setChild(mover).
					setType("binding").
					setName("std");
			
			// Add mover to new parent's bindings
			List<Link> bindings = this.linkService.getBindings(newParent.getId());
			
			if (mode.equals(MOVE_OVER)) {
				bindings.add(moverLink);
				LOG.debug("Added mover to end of new parent's existing bindings");	
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
					LOG.debug("Inserted mover into new parent's existing bindings");	
				}
				else {
					bindings.add(moverLink);
					LOG.warn("Failed to determine point of insertion - placed at end");	
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
		else {
			LOG.error(compose("Failed to identify parent of", mover.getPath()));
		}
		
		return false;
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
