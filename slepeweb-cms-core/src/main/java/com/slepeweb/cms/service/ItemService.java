package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.ResourceException;


public interface ItemService {
	List<Item> getAllVersions(Long origId);
	void deleteAllVersions(Long origId);
	Item getEditableVersion(Long origId);
	Item getPublishedVersion(Long origId);
	Item getItem(Long siteId, String path);
	Item getEditableVersion(Long siteId, String path);
	List<Item> getItemsByPathLike(Long siteId, String path);
	Item getItem(Long id);
	Item getItemByOriginalId(Long id);
	Item getItem(Long id, int version);
	Item save(Item i) throws ResourceException;
	void saveFieldValues(Item i) throws ResourceException;
	void saveLinks(Item i) throws ResourceException, DuplicateItemException;
	int getCount();
	int getCount(String path);
	int getCountByType(Long itemTypeId);
	int getVersionCount(long origid);
	boolean move(Item child, Item currentParent, Item targetParent, Item target) throws ResourceException;
	boolean move(Item child, Item currentParent, Item targetParent, Item target, 
			String mode) throws ResourceException;
	int trashItemAndDirectChildren(Item i);
	Item restoreItem(Long id);
	List<Item> getTrashedItems();
	int deleteTrashedItems(long[] idArr);
	int restoreSelectedItems(long[] idArr);
	int getBinCount();
	Item copy(Item source, String name, String simplename) throws ResourceException;
	Item version(Item source) throws ResourceException;
	Item revert(Item source) throws ResourceException;
	int getCountByPath(Long siteId, String path);
	boolean updatePublished(Long id, boolean b);
	boolean updateSearchable(Long id, boolean b);
}
