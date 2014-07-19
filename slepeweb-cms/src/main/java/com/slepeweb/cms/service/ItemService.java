package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;


public interface ItemService {
	void insertItem(Item i);
	void updateItem(Item i);
	void deleteItem(Long id);
	Item getItem(Long siteId, String path);
	Item getItem(Long id);
	ItemType getItemType(Item i);
	Site getSite(Item i);
}
