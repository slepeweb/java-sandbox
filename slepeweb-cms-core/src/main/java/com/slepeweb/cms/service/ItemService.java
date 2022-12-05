package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.except.ResourceException;


public interface ItemService {
	List<Item> getAllVersions(Long origId);
	void deleteItem(Long origId, int version);
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
	int getCount();
	int getCount(String path);
	int getCountByType(Long itemTypeId);
	int getVersionCount(long origid);
	int trashItemAndDirectChildren(Item i);
	List<Item> getTrashedItems();
	int deleteTrashedItems(long[] idArr);
	int restoreSelectedItems(long[] idArr);
	int getBinCount();
	int getCountByPath(Long siteId, String path);
	boolean updatePublished(Long id, boolean b);
	boolean updateSearchable(Long id, boolean b);
	void updateDescendantPaths(String moverPath, String newChildPath);
	void updateItemPath(Long moverId, String newChildPath);
	void updateOrigId(Item ni);
	void updateEditable(Item i);
}
