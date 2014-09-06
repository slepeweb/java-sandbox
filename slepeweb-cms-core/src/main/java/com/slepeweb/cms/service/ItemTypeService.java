package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.ItemType;


public interface ItemTypeService {
	void deleteItemType(ItemType it);
	ItemType getItemType(String name);
	ItemType getItemType(Long id);
	List<ItemType> getAvailableItemTypes();
	ItemType save(ItemType it);
	int getCount();
}
