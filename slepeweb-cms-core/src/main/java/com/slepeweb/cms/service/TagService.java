package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Tag;
import com.slepeweb.cms.bean.TagList;


public interface TagService {
	void deleteTags(Long itemId);
	List<Tag> getTags4Item(Long itemId);
	TagList getTagCount4Site(Long siteId, int max);
	List<String> getDistinctTagValues4Site(Long siteId);
	List<Tag> getTags4SiteWithValue(Long siteId, String value);
	Tag getTag4ItemWithValue(Long itemId, String value);
	void save(Item i, String valueStr);
	void save(Item i, List<String> values);
}
