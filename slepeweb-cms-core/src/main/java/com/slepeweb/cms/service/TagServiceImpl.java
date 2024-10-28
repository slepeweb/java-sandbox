package com.slepeweb.cms.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Tag;
import com.slepeweb.cms.bean.TagCount;
import com.slepeweb.cms.bean.TagList;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class TagServiceImpl extends BaseServiceImpl implements TagService {
	
	private static Logger LOG = Logger.getLogger(TagServiceImpl.class);
	private static String SELECT_TEMPLATE = 
			"select * from tag " +
			"where %s ";
	
	@Autowired private SolrService4Cms solrService4Cms;
	
	// Saves tags represented by a comma-delimited string
	public void save(Item i, String valueStr) {
		deleteTags(i.getId());
		
		for (String value : valueStr.split("[ ,]+")) {
			if (StringUtils.isNotBlank(value)) {
				insert(i.getSite().getId(), i.getId(), value);
			}
		}
		
		this.solrService4Cms.save(i.setTags(null));
	}
	
	// Saves tags in a string collection
	public void save(Item i, List<String> values) {
		deleteTags(i.getId());
		
		for (String value : values) {
			insert(i.getSite().getId(), i.getId(), value);
		}
		 
		this.solrService4Cms.save(i.setTags(null));
	}
	
	private void insert(Long siteId, Long itemId, String value) {
		this.jdbcTemplate.update(
			"insert into tag (siteid, itemid, value) values (?, ?, ?)", 
			siteId, itemId, value);
		
		LOG.info(String.format("Tagged item '%d' [%s]", itemId, value));
	}

	public void deleteTags(Long itemId) {
		if (this.jdbcTemplate.update("delete from tag where itemid = ?", itemId) > 0) {
			LOG.warn(compose("Deleted tags", String.valueOf(itemId)));
		}
	}
	
	
	public List<Tag> getTags4Item(Long itemId) {
		return this.jdbcTemplate.query(String.format(SELECT_TEMPLATE, "itemid = ?"), 
				new RowMapperUtil.TagMapper(), itemId);
	}
	
	public List<String> getDistinctTagValues4Site(Long siteId) {
		return this.jdbcTemplate.query("select distinct(value) from tag where siteid = ? order by value", 
				new RowMapperUtil.TagValueMapper(), siteId);
	}
	
	public TagList getTagCount4Site(Long siteId, int max) {
		List<TagCount> rawList = this.jdbcTemplate.query("select value from tag where siteid = ?", 
				new RowMapperUtil.TagCountMapper(), siteId);
		
		TagList tags = new TagList(max);
		
		for (TagCount next : rawList) {
			tags.inc(next);
		}
		
		return tags;
	}
	
	public List<Tag> getTags4SiteWithValue(Long siteId, String value) {
		return this.jdbcTemplate.query(String.format(SELECT_TEMPLATE, "siteid = ? and value = ?"), 
				new RowMapperUtil.TagMapper(), siteId, value);
	}
	
	public Tag getTag4ItemWithValue(Long itemId, String value) {
		return (Tag) getFirstInList(
			this.jdbcTemplate.query(String.format(SELECT_TEMPLATE, "itemid = ? and value = ?"), 
				new RowMapperUtil.TagMapper(), itemId, value));
	}
	
}
