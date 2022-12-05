package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.MoverItem;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.ResourceException;

public interface ItemWorkerService {
	MoverItem move(MoverItem child) throws ResourceException;
	Item copy(Item source, String name, String simplename) throws ResourceException;
	Item version(Item source) throws ResourceException;
	void saveFieldValues(Item i) throws ResourceException;
	void saveLinks(Item i) throws ResourceException, DuplicateItemException;
	Item restoreItem(Long id);
	Item revert(Item source) throws ResourceException;
}
