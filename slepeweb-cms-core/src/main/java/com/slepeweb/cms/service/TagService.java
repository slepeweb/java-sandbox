package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Tag;


public interface TagService {
	void deleteTags(Long itemId);
	List<Tag> getTags4Item(Long itemId);
	List<String> getTagValues4Site(Long siteId);
	List<Tag> getTags4SiteWithValue(Long siteId, String value);
	Tag getTag4ItemWithValue(Long itemId, String value);
	void save(Item i, String valueStr);
	void save(Item i, List<String> values);
}
