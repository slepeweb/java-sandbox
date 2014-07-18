package com.slepeweb.cms.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class ItemServiceImpl extends BaseServiceImpl implements ItemService {
	
	private static Logger LOG = Logger.getLogger(ItemServiceImpl.class);
	private String columns = "name, simplename, path, siteid, typeid, datecreated, dateupdated, deleted";
	
	@Autowired private SiteService siteService;
	@Autowired private ItemTypeService itemTypeService;
	
	public void addItem(Item i) {
		this.jdbcTemplate.update(
				String.format("insert into item (%s) values (%s)", columns, placeholders4Insert(columns)),
				i.getName(), i.getSimpleName(), i.getPath(), i.getSite().getId(), i.getType().getId(), 
				i.getDateCreated(), i.getDateUpdated(), false);
		
		// TODO: Fields?
		LogUtil.info(LOG, "Added new item", i.getPath());
	}

	public void updateItem(Item i) {
		Item dbRecord = getItem(i.getId());
		
		if (dbRecord != null) {
			dbRecord.assimilate(i);
			
			this.jdbcTemplate.update(
					String.format("update item set %s where id = ?", placeholders4Update(columns)),
					dbRecord.getName(), dbRecord.getSimpleName(), dbRecord.getPath(), 
					dbRecord.getSite().getId(), dbRecord.getType().getId(), 
					dbRecord.getDateCreated(), dbRecord.getDateUpdated(), dbRecord.isDeleted());
			
			LogUtil.info(LOG, "Updated item", i.getPath());
		}
		else {
			LogUtil.warn(LOG, "Item not found", i.getPath());
		}
	}

	public void deleteItem(Long id) {
		if (this.jdbcTemplate.update("delete from item where id = ?", id) > 0) {
			LogUtil.warn(LOG, "Deleted item", String.valueOf(id));
		}
	}

	public void deleteItem(Item i) {
		deleteItem(i.getId());
	}

	public Item getItem(Long siteId, String path) {
		return getItem("select * from item where siteid = ? and path = ?", new Object[]{siteId, path});
	}

	public Item getItem(Long id) {
		return getItem("select * from item where id = ?", new Object[]{id});
	}
	
	private Item getItem(String sql, Object[] params) {
		List<Item> group = this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.ItemMapper());
		
		if (group.size() > 0) {
			return group.get(0);
		}
		else {
			return null;
		}
	}

	public ItemType getItemType(Item item) {
		if (StringUtils.isBlank(item.getType().getName())) {
			item.setType(this.itemTypeService.getItemType(item.getType().getId()));
		}
		return item.getType();
	}

	public Site getSite(Item item) {
		if (StringUtils.isBlank(item.getSite().getName())) {
			item.setSite(this.siteService.getSite(item.getSite().getId()));
		}
		return item.getSite();
	}

}
