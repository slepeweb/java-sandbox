package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.except.ResourceException;


public interface ItemService {
	void deleteAllVersions(Long origId);
	Item getItem(Long siteId, String path);
	List<Item> getItemsByPathLike(Long siteId, String path);
	Item getItem(Long id);
	Item getItemByOriginalId(Long id);
	Item getItem(Long id, int version);
	Item save(Item i) throws ResourceException;
	Item save(Item i, boolean extended) throws ResourceException;
	void saveFieldValues(List<FieldValue> fvs) throws ResourceException;
	void saveLinks(Item i) throws ResourceException;
	int getCount();
	int getCount(String path);
	int getCountByType(Long itemTypeId);
	int getVersionCount(long origid);
	boolean move(Item child, Item currentParent, Item targetParent, Item target, 
			boolean moverIsShortcut) throws ResourceException;
	boolean move(Item child, Item currentParent, Item targetParent, Item target, 
			boolean moverIsShortcut, String mode) throws ResourceException;
	Item trashItem(Long id);
	Item restoreItem(Long id);
	List<Item> getTrashedItems();
	int deleteTrashedItems(long[] idArr);
	int restoreSelectedItems(long[] idArr);
	int getBinCount();
	Item copy(Item source, String name, String simplename) throws ResourceException;
	Item version(Item source) throws ResourceException;
	Item revert(Item source) throws ResourceException;
}
