package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;


public interface ItemService {
	void deleteItem(Long id);
	Item getItem(Long siteId, String path);
	Item getItem(Long id);
	Item save(Item i);
	Item save(Item i, boolean extended);
	void saveFieldValues(List<FieldValue> fvs);
	void saveLinks(List<Link> links);
	int getCount();
	int getCount(String path);
	int getCountByType(Long itemTypeId);
	void move(Item child, Item newParent);
	Item trashItem(Long id);
	Item restoreItem(Long id);
	int getBinCount();
}
