package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Tag;


public interface TagService {
	void deleteTags(Long itemId);
	Tag getTag(Long itemId, String value);
	List<String> getTagValues(Long itemId);
	Item getTaggedItem(String value);
	List<Item> getTaggedItems(String value);
	void save(Long itemId, String valueStr);
	void save(Long itemId, List<String> values);
}
