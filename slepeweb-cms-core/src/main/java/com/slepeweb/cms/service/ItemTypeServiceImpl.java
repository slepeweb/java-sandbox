package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class ItemTypeServiceImpl extends BaseServiceImpl implements ItemTypeService {
	
	private static Logger LOG = Logger.getLogger(ItemTypeServiceImpl.class);
	
	public ItemType save(ItemType it) {
		boolean updated = false;
		
		if (it.isDefined4Insert()) {
			ItemType dbRecord = getItemType(it.getName());		
			if (dbRecord != null) {
				updateItemType(dbRecord, it);
				updated = true;
			}
			else {
				insertItemType(it);
			}
			
			saveFieldsForType(it);
			// TODO: need to remove old fields - similar to old links.
			// But for the moment, consider field removal from an item type out of scope.
			
			if (updated) {
				return dbRecord;
			}
		}
		else {
			LOG.error(compose("ItemType not saved - insufficient data", it));
		}
		
		return it;
	}
	
	private void insertItemType(ItemType it) {
		this.jdbcTemplate.update(
				"insert into itemtype (name, mimetype, privatecache, publiccache) values (?, ?, ?, ?)", 
				it.getName(), it.getMimeType(), it.getPrivateCache(), it.getPublicCache());
		
		it.setId(getLastInsertId());
		this.cacheEvictor.evict(it);
		LOG.info(compose("Added new item type", it));
	}

	private void updateItemType(ItemType dbRecord, ItemType it) {
		if (! dbRecord.equals(it)) {
			this.cacheEvictor.evict(dbRecord);
			dbRecord.assimilate(it);
			
			this.jdbcTemplate.update(
					"update itemtype set name = ?, mimetype = ?, privatecache = ?, publiccache = ? where id = ?", 
					dbRecord.getName(), dbRecord.getMimeType(), 
					dbRecord.getPrivateCache(), dbRecord.getPublicCache(), dbRecord.getId());
			
			LOG.info(compose("Updated item type", it));
		}
		else {
			it.setId(dbRecord.getId());
			LOG.info(compose("Item type not modified", it));
		}
	}
	
	private void saveFieldsForType(ItemType it) {
		if (it.getFieldsForType() != null) {
			for (FieldForType fft : it.getFieldsForType()) {
				fft.save();
			}
		}
		else {
			LOG.debug(compose("No fields defined for this type", it));
		}
	}

	public void deleteItemType(ItemType it) {
		if (this.jdbcTemplate.update("delete from itemtype where id = ?", it.getId()) > 0) {
			LOG.warn(compose("Deleted item type", it.getId()));
			deepEvict(it);
		}
	}
	
	private void deepEvict(ItemType it) {
		this.cacheEvictor.evict(it);
		
		for (FieldForType fft : it.getFieldsForType()) {
			this.cacheEvictor.evict(fft);
		}
	}
	
	@Cacheable(value="serviceCache")
	public ItemType getItemType(String name) {
		return getItemType("select * from itemtype where name = ?", new Object[]{name});
	}

	@Cacheable(value="serviceCache")
	public ItemType getItemType(Long id) {
		return getItemType("select * from itemtype where id = ?", new Object[]{id});
	}
	
	private ItemType getItemType(String sql, Object[] params) {
		return (ItemType) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.ItemTypeMapper()));
	}

	@Cacheable(value="serviceCache")
	public List<ItemType> getAvailableItemTypes() {
		return this.jdbcTemplate.query("select * from itemtype order by name", 
				new Object[]{}, new RowMapperUtil.ItemTypeMapper());
	}
	
	@SuppressWarnings("deprecation")
	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from itemtype");
	}

}
