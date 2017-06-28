package com.slepeweb.commerce.service;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.commerce.bean.Product;

public interface ProductService extends ItemService {
	Product copy(Product source, String name, String simplename, String partNum, Integer copyId) 
			throws MissingDataException, DuplicateItemException, ResourceException;
	Product get(Long origItemId);
	Product get(String partNum);
	Product save(Product p) 
			throws MissingDataException, DuplicateItemException, ResourceException;
	long count();
}
