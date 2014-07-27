package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.Link.LinkType;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class ItemServiceImpl extends BaseServiceImpl implements ItemService {
	
	private static Logger LOG = Logger.getLogger(ItemServiceImpl.class);
	private static final String SELECT_TEMPLATE = 
			"select i.*, s.name as sitename, s.hostname, it.name as typename from " +
			"item i, site s, itemtype it where " +
			"i.siteid=s.id and i.typeid=it.id and %s and i.deleted=0";
	
	@Autowired protected LinkService linkService;
	@Autowired protected FieldValueService fieldValueService;

	private String columns = "name, simplename, path, siteid, typeid, datecreated, dateupdated, deleted";
	
	public Item save(Item i) {
		if (i.isDefined4Insert()) {
			Item dbRecord = getItem(i.getSite().getId(), i.getPath());		
			if (dbRecord != null) {
				updateItem(dbRecord, i);
			}
			else {
				insertItem(i);
			}
			
			saveFieldValues(i);
			saveChildLinks(i);
			removeOldChildLinks(dbRecord, i);
		}
		
		return i;
	}
	
	private void insertItem(Item i) {
		// Parent item exists?
		String parentPath = getParentPath(i.getPath());
		Item parentItem = getItem(i.getSite().getId(), parentPath);
		
		if (i.isRoot() || parentItem != null) {
			
			// Item table
			this.jdbcTemplate.update(
					String.format("insert into item (%s) values (%s)", columns, placeholders4Insert(columns)),
					i.getName(), i.getSimpleName(), i.getPath(), i.getSite().getId(), i.getType().getId(), 
					i.getDateCreated(), i.getDateUpdated(), false);				
			
			Long lastId = getLastInsertId();
			i.setId(lastId);
			LOG.info(compose("Added new item", i.getPath()));
			
			// Insert binding link to parent item
			if (! i.isRoot()) {
				Item childItem = getItem(lastId);
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
		}
		else {
			LOG.warn(compose("Parent item not found", parentPath));
		}
	}

	private void updateItem(Item dbRecord, Item i) {
		if (! dbRecord.equals(i)) {
			dbRecord.assimilate(i);
			
			this.jdbcTemplate.update(
					String.format("update item set %s where id = ?", placeholders4Update(this.columns)),
					dbRecord.getName(), dbRecord.getSimpleName(), dbRecord.getPath(), 
					dbRecord.getSite().getId(), dbRecord.getType().getId(), 
					dbRecord.getDateCreated(), dbRecord.getDateUpdated(), dbRecord.isDeleted(), i.getId());
			
			LOG.info(compose("Updated item", i.getPath()));
		}
		else {
			i.setId(dbRecord.getId());
			LOG.info(compose("Item not modified", i.getPath()));
		}
		
		// TODO: Simplename or binding may have changed.
		// Either way, all child (binding) descendants will need their path properties updated
	}
	
	private void saveFieldValues(Item i) {
		if (i.getFieldValues() != null) {
			for (FieldValue fv : i.getFieldValues()) {
				fv.save();
			}
		}
	}

	private void saveChildLinks(Item i) {
		if (i.getLinks() != null) {
			for (Link l : i.getLinks()) {
				l.save();
			}
		}
	}
	
	private void removeOldChildLinks(Item dbRecord, Item i) {
		if (dbRecord != null && dbRecord.getLinks() != null && i.getLinks() != null) {
			for (Link dbLink : dbRecord.getLinks()) {
				if (! i.getLinks().contains(dbLink)) {
					// TODO: Cautionary move: dbLink.delete();
					LOG.info(compose("Deleted old child link", dbLink.getParentId(), dbLink.getChild().getPath()));
				}
			}
		}
	}

	public void deleteItem(Long id) {
		if (this.jdbcTemplate.update("delete from item where id = ?", id) > 0) {
			LOG.warn(compose("Deleted item", String.valueOf(id)));
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
	
	private Item getItem(String sql, Object[] params) {
		List<Item> group = this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.ItemMapper());
		
		if (group.size() > 0) {
			return group.get(0);
		}
		return null;
	}

	private String getParentPath(String path) {
		int c = path.lastIndexOf("/");
		if (c > 0) {
			return path.substring(0, c);
		}
		return "/";
	}
}
