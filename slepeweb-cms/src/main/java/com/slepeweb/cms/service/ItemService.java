package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;


public interface ItemService {
	void deleteItem(Long id);
	Item getItem(Long siteId, String path);
	Item getItem(Long id);
	Item save(Item i);
	int getCount();
	int getCount(String path);
	int getCountByType(Long itemTypeId);
	void move(Item child, Item newParent);
	Item trashItem(Long id);
	Item restoreItem(Long id);
	int getBinCount();
}
