package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.ItemType;


public interface ItemTypeService {
	void addItemType(ItemType it);
	void updateItemType(ItemType it);
	void deleteItemType(Long id);
	ItemType getItemType(String name);
	ItemType getItemType(Long id);
}
