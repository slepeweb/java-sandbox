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
	private static final String MOVE_BEFORE = "before";
	private static final String MOVE_AFTER = "after";
	private static final String MOVE_OVER = "over";

	private static final String SELECT_TEMPLATE = 
			"select i.*, s.name as sitename, s.hostname, it.id as typeid, it.name as typename, it.mimetype, " +
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
		if (i.isDefined4Insert()) {
			Item dbRecord = getItem(i.getId());		
			if (dbRecord != null) {
				updateItem(dbRecord, i);
			}
			else {
				insertItem(i);
			}
			
			if (extendedSave) {
				saveFieldValues(i.getFieldValues());
				saveLinks(i, dbRecord);
				saveMedia(i);
			}
		}
		
		return i;
	}
	
	private void insertItem(Item i) {
		// Item table
		this.jdbcTemplate.update(
				"insert into item (name, simplename, path, siteid, typeid, templateid, datecreated, dateupdated, deleted) " +
				"values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
				i.getName(), i.getSimpleName(), i.getPath(), i.getSite().getId(), i.getType().getId(), 
				i.getTemplate() == null ? 0 : i.getTemplate().getId(), i.getDateCreated(), i.getDateUpdated(), false);				
		
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
					"update item set name = ?, simplename = ?, path = ?, templateid = ?, dateupdated = ?, deleted = ? where id = ?",
					dbRecord.getName(), dbRecord.getSimpleName(), dbRecord.getPath(), 
					dbRecord.getTemplate() == null ? 0 : dbRecord.getTemplate().getId(), 
					dbRecord.getDateUpdated(), dbRecord.isDeleted(), i.getId());
			
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
	
	private void saveMedia(Item i) {
		if (i.getMediaUploadFilePath() != null && i.getType().isMedia()) {
			this.mediaService.save(i);
		}
	}
	
	private void saveDefaultFieldValues(Item i) {
		// If item has no field values, create them, with default values
		if (i.getFieldValues() == null || i.getFieldValues().size() == 0) {
			i.setFieldValues(new ArrayList<FieldValue>());
			FieldValue fv;

			for (FieldForType fft : this.fieldForTypeService.getFieldsForType(i.getType().getId())) {
				fv = CmsBeanFactory.makeFieldValue().
					setField(fft.getField()).
					setItemId(i.getId()).
					setValue(fft.getField().getDefaultValue()).
					setDateUpdated(new Timestamp(System.currentTimeMillis()));
				
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
				if (! updatedLinks.contains(dbLink) && dbLink.getType() != LinkType.binding) {
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
			String.format(SELECT_TEMPLATE, "i.siteid=? and i.path=? and i.deleted=0"),
			new Object[]{siteId, path});
	}

	public Item getItem(Long id) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.id=? and i.deleted=0"), 
			new Object[]{id});
	}
	
	public Item getItemFromBin(Long id) {
		return getItem(
			String.format(SELECT_TEMPLATE, "i.id=? and i.deleted=1"), 
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
	
//	/*
//	 * This method can be used if a child item moves to a different parent, but  
//	 * NOT if the child's simplename has changed.
//	 */
//	public Item move(Item child, Item newParent) {
//		Link parentLink = this.linkService.getParent(child.getId());
//		if (parentLink != null) {
//			// Un-link from current path
//			this.linkService.deleteLinks(parentLink.getParentId(), child.getId());
//			
//			// Bind to new parent
//			parentLink.setParentId(newParent.getId()).setOrdering(1);
//			parentLink.save();
//			
//			// Update paths of descendant items
//			String divider = newParent.isRoot() ? "" : "/";
//			String newChildPath = newParent.getPath() + divider + child.getSimpleName();
//			updateDescendantPaths(child.getPath(), newChildPath);
//			
//			// Update child item path
//			updateItemPath(child.getId(), newChildPath);
//			
//			// Force newParent links to be re-calculated, since they have now changed
//			newParent.setLinks(null);
//		}
//		else {
//			LOG.error(compose("Failed to identify parent item", child.getPath()));
//		}
//		
//		return child;
//	}

	public Item move(Item mover, Item parent) {
		return move(mover, parent, "over");
	}
	
	/*
	 * This provides a relative move, ie before/after target.
	 * If mode == "over", then target is effectively a new parent.
	 */
	public Item move(Item mover, Item target, String mode) {
		Link oldParentage = this.linkService.getParent(mover.getId());
		Link moverLink = oldParentage;
		
		if (oldParentage != null) {
			Item targetParent = target.getParent();
			
			// Break the parent link for the mover, even if it it is moving amongst its siblings
			this.linkService.deleteLinks(oldParentage.getParentId(), mover.getId());
			
			// Bind to new parent - we'll save teh mover link later
			if (mode.equals(MOVE_OVER)) {
				moverLink.setParentId(target.getId());
			}
			else {
				moverLink.setParentId(targetParent.getId());
			}
			
			// Insert the mover alongside its siblings, in the correct order
			List<Link> bindings;
			if (mode.equals(MOVE_OVER)) {
				// Append mover to end of list of children
				bindings = this.linkService.getBindings(target.getId());
				bindings.add(moverLink);
			}
			else {
				// Append mover before/after target item
				bindings = this.linkService.getBindings(targetParent.getId());
				
				// First identify the insertion point in the bindings list
				int cursor = -1;
				for (Link l : bindings) {
					if (l.getChild().getId() == target.getId()) {
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
				}
				else {
					bindings.add(moverLink);
				}
			}
			
			// Re-order bindings from 0, then save
			int cursor = 0;
			for (Link l : bindings) {
				if (l.getOrdering() != cursor) {
					l.setOrdering(cursor);
					l.save();
				}
				cursor++;
			}
			
			// Update paths of descendant items
			String divider = target.isRoot() ? "" : "/";
			String newChildPath = target.getPath() + divider + mover.getSimpleName();
			updateDescendantPaths(mover.getPath(), newChildPath);
			
			// Update child item path
			updateItemPath(mover.getId(), newChildPath);
			
			// Force newParent links to be re-calculated, since they have now changed
			target.setLinks(null);
		}
		else {
			LOG.error(compose("Failed to identify parent item", mover.getPath()));
		}
		
		return mover;
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
