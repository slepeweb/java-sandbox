package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Tag;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class TagServiceImpl extends BaseServiceImpl implements TagService {
	
	private static Logger LOG = Logger.getLogger(TagServiceImpl.class);
	private static String SELECT_TEMPLATE = 
			"select * from tag " +
			"where %s ";
	
	// Saves tags represented by a comma-delimited string
	public void save(Long siteId, Long itemId, String valueStr) {
		deleteTags(itemId);
		
		for (String value : valueStr.split("[ ,]+")) {
			insert(siteId, itemId, value);
		}
	}
	
	// Saves tags in a string collection
	public void save(Long siteId, Long itemId, List<String> values) {
		deleteTags(itemId);
		
		for (String value : values) {
			insert(siteId, itemId, value);
		}
	}
	
	private void insert(Long siteId, Long itemId, String value) {
		this.jdbcTemplate.update(
			"insert into tag (siteid, itemid, value) values (?, ?, ?)", 
			siteId, itemId, value);
		
		LOG.info(String.format("Tagged item '%d' [%s]", itemId, value));
	}

	/*
	 * This method is called every time that the tags for an item are updated.
	 * This makes it a suitable place to invoke cache eviction.
	 */
	public void deleteTags(Long itemId) {
		
		List<Tag> oldTags = getTags4Item(itemId);
		
		if (this.jdbcTemplate.update("delete from tag where itemid = ?", itemId) > 0) {
			LOG.warn(compose("Deleted tags", String.valueOf(itemId)));
		}
		
		for (Tag tg : oldTags) {
			this.cacheEvictor.evict(tg);
		}
	}
	
	
	public List<Tag> getTags4Item(Long itemId) {
		return this.jdbcTemplate.query(String.format(SELECT_TEMPLATE, "itemid = ?"), 
				new Object[]{itemId},
				new RowMapperUtil.TagMapper());
	}
	
	public List<String> getTagValues4Site(Long siteId) {
		return this.jdbcTemplate.query("select distinct(value) from tag where siteid = ? order by value", 
				new Object[]{siteId},
				new RowMapperUtil.TagValueMapper());
	}
	
	public List<Tag> getTags4SiteWithValue(Long siteId, String value) {
		return this.jdbcTemplate.query(String.format(SELECT_TEMPLATE, "siteid = ? and value = ?"), 
				new Object[]{siteId, value},
				new RowMapperUtil.TagMapper());
	}
	
	public Tag getTag4ItemWithValue(Long itemId, String value) {
		return (Tag) getFirstInList(
			this.jdbcTemplate.query(String.format(SELECT_TEMPLATE, "itemid = ? and value = ?"), 
				new Object[]{itemId, value},
				new RowMapperUtil.TagMapper()));
	}
	
}
