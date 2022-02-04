package com.slepeweb.commerce.service;

import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.commerce.bean.Product;

public interface ProductService extends ItemService {
	Product copy(Product source, String name, String simplename, String partNum, Integer copyId) 
			throws ResourceException;
	Product get(Long origItemId);
	Product get(Long siteId, String partNum);
	Product save(Product p) throws ResourceException;
	long count();
}
