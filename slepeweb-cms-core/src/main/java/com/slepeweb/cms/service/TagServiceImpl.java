package com.slepeweb.cms.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Tag;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class TagServiceImpl extends BaseServiceImpl implements TagService {
	
	private static Logger LOG = Logger.getLogger(TagServiceImpl.class);
	private static String SELECT_TEMPLATE = 
			"select tg.value as tagvalue, i.*, s.name as sitename, s.shortname as site_shortname, s.language, s.xlanguages, s.secured, " +
			"it.id as typeid, it.name as typename, it.mimetype, it.privatecache, it.publiccache, " +
			"t.id as templateid, t.name as templatename, t.forward " +
			"from item i " +
			"join tag tg on tg.itemid=i.id " +
			"join site s on i.siteid=s.id " +
			"join itemtype it on i.typeid=it.id " +
			"left join template t on i.templateid=t.id " +
			"where %s and i.deleted=0 ";

	
	public void save(Item i, String valueStr) {
		deleteTags(i.getId());
		
		for (String value : valueStr.split("[ ,]+")) {
			insert(i, value);
		}
	}
	
	public void save(Item i, List<String> values) {
		deleteTags(i.getId());
		
		for (String value : values) {
			insert(i, value);
		}
	}
	
	private void insert(Item i, String value) {
		this.jdbcTemplate.update(
			"insert into tag (siteid, itemid, value) values (?, ?, ?)", 
			i.getSite().getId(), i.getId(), value);
		
		LOG.info(String.format("Tagged item '%s' [%s]", i, value));
	}

	/*
	 * This method is called every time that the tags for an item are updated.
	 * This makes it a suitable place to invoke cache eviction.
	 */
	public void deleteTags(Long id) {
		
		List<Tag> oldTags = getTags(id);
		
		if (this.jdbcTemplate.update("delete from tag where itemid = ?", id) > 0) {
			LOG.warn(compose("Deleted tags", String.valueOf(id)));
		}
		
		for (Tag tg : oldTags) {
			this.cacheEvictor.evict(tg);
		}
	}
	
	
	public List<Tag> getTags(Long itemId) {
		return this.jdbcTemplate.query(String.format(SELECT_TEMPLATE, "tg.itemid = ?"), 
				new Object[]{itemId},
				new RowMapperUtil.TagMapper());
	}
	
	public List<Tag> getTags(Long siteId, String value) {
		return this.jdbcTemplate.query(String.format(SELECT_TEMPLATE, "tg.siteid = ? and tg.value = ?"), 
				new Object[]{siteId, value},
				new RowMapperUtil.TagMapper());
	}
	
	public Tag getTag(Long itemId, String value) {
		return (Tag) getFirstInList(
			this.jdbcTemplate.query(String.format(SELECT_TEMPLATE, "tg.itemid = ? and tg.value = ?"), 
				new Object[]{itemId, value},
				new RowMapperUtil.TagMapper()));
	}
	
	/* 
	 * This method should only be called for tag values which are only expected
	 * to be assigned to one item. These responses are cached, and the cache needs
	 * to be cleared should the tags corresponding to a matched item get updated.
	 */
	@Cacheable(value="serviceCache")
	public Item getTaggedItem(Long siteId, String value) {
		Tag tag =  (Tag) getFirstInList(getTags(siteId, value));		
		if (tag != null) {
			return tag.getItem();
		}		
		return null;
	}

	public List<Item> getTaggedItems(Long siteId, String value) {
		List<Tag> tags = getTags(siteId, value);		
		List<Item> items = new ArrayList<Item>(tags.size());
		for (Tag tag : tags) {
			items.add(tag.getItem());
		}
		return items;
	}

	public List<String> getTagValues(Long itemId) {
		List<Tag> tags = getTags(itemId);		
		List<String> values = new ArrayList<String>(tags.size());
		for (Tag tag : tags) {
			values.add(tag.getValue());
		}
		return values;
	}

}
