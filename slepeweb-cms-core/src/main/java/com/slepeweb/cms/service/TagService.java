package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Tag;


public interface TagService {
	void deleteTags(Long itemId);
	Tag getTag(Long itemId, String value);
	List<String> getTagValues(Long itemId);
	Item getTaggedItem(Long siteId, String value);
	List<Item> getTaggedItems(Long siteId, String value);
	void save(Item i, String valueStr);
	void save(Item i, List<String> values);
}
