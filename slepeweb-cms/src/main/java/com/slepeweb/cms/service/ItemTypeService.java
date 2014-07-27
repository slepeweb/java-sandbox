package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.ItemType;


public interface ItemTypeService {
	void deleteItemType(Long id);
	ItemType getItemType(String name);
	ItemType getItemType(Long id);
	ItemType save(ItemType it);
}
