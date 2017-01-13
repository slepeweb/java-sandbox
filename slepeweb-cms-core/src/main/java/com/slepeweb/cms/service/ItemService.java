package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.except.NotVersionableException;


public interface ItemService {
	void deleteItem(Long id);
	Item getItem(Long siteId, String path);
	Item getItem(Long id);
	Item getItemByOriginalId(Long id);
	Item getItem(Long id, int version);
	Item save(Item i);
	Item save(Item i, boolean extended);
	void saveFieldValues(List<FieldValue> fvs);
	void saveLinks(Item i);
	int getCount();
	int getCount(String path);
	int getCountByType(Long itemTypeId);
	int getVersionCount(long origid);
	boolean move(Item child, Item currentParent, Item newParent, boolean shortcut);
	boolean move(Item child, Item currentParent, Item target, boolean shortcut, String mode);
	Item trashItem(Long id);
	Item restoreItem(Long id);
	int getBinCount();
	Item copy(Item source, String name, String simplename);
	Item version(Item source) throws NotVersionableException;
	Item revert(Item source);
}
