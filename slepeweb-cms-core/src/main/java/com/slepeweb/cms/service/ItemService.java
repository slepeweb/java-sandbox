package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.NotRevertableException;
import com.slepeweb.cms.except.NotVersionableException;
import com.slepeweb.cms.except.ResourceException;


public interface ItemService {
	void deleteItem(Long id);
	Item getItem(Long siteId, String path);
	Item getItem(Long id);
	Item getItemByOriginalId(Long id);
	Item getItem(Long id, int version);
	Item save(Item i) throws MissingDataException, DuplicateItemException;
	Item save(Item i, boolean extended) throws MissingDataException, DuplicateItemException;
	void saveFieldValues(List<FieldValue> fvs) throws MissingDataException;
	void saveLinks(Item i) throws MissingDataException;
	int getCount();
	int getCount(String path);
	int getCountByType(Long itemTypeId);
	int getVersionCount(long origid);
	boolean move(Item child, Item currentParent, Item newParent, boolean shortcut) 
			throws MissingDataException, ResourceException;
	boolean move(Item child, Item currentParent, Item target, boolean shortcut, String mode) 
			throws MissingDataException, ResourceException;
	Item trashItem(Long id);
	Item restoreItem(Long id);
	List<Item> getTrashedItems();
	int deleteTrashedItems(long[] idArr);
	int restoreSelectedItems(long[] idArr);
	int getBinCount();
	Item copy(Item source, String name, String simplename) throws MissingDataException, DuplicateItemException;
	Item version(Item source) throws NotVersionableException, MissingDataException, DuplicateItemException;
	Item revert(Item source) throws NotRevertableException;
}
