package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;


public interface ItemService {
	void deleteItem(Long id);
	Item getItem(Long siteId, String path);
	Item getItem(Long id);
	Item save(Item i);
	Item save(Item i, boolean extended);
	void saveFieldValues(List<FieldValue> fvs);
	void saveLinks(Item i);
	int getCount();
	int getCount(String path);
	int getCountByType(Long itemTypeId);
	Item move(Item child, Item newParent);
	Item move(Item child, Item target, String mode);
	Item trashItem(Long id);
	Item restoreItem(Long id);
	int getBinCount();
}