package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class ItemTypeServiceImpl extends BaseServiceImpl implements ItemTypeService {
	
	private static Logger LOG = Logger.getLogger(ItemTypeServiceImpl.class);
	
	public void addItemType(ItemType it) {
		this.jdbcTemplate.update(
				"insert into itemtype (name) values (?)", 
				it.getName());

		LogUtil.info(LOG, "Added new item type", it.getName());
	}

	public void updateItemType(ItemType it) {
		ItemType dbRecord = getItemType(it.getId());
		
		if (dbRecord != null) {
			dbRecord.assimilate(it);
			
			this.jdbcTemplate.update(
					"update itemtype set name = ? where id = ?", 
					dbRecord.getName(), dbRecord.getId());
			
			LogUtil.info(LOG, "Updated item type", it.getName());
		}
		else {
			LogUtil.warn(LOG, "Item type not found", it.getName());
		}
	}

	public void deleteItemType(Long id) {
		if (this.jdbcTemplate.update("delete from itemtype where id = ?", id) > 0) {
			LogUtil.warn(LOG, "Deleted item type", String.valueOf(id));
		}
	}

	public ItemType getItemType(String name) {
		return getItemType("select * from itemtype where name = ?", new Object[]{name});
	}

	public ItemType getItemType(Long id) {
		return getItemType("select * from itemtype where id = ?", new Object[]{id});
	}
	
	private ItemType getItemType(String sql, Object[] params) {
		List<ItemType> group = this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.ItemTypeMapper());
		
		if (group.size() > 0) {
			return group.get(0);
		}
		else {
			return null;
		}
	}

}
