package com.slepeweb.commerce.service;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.commerce.bean.Product;

public interface ProductService {
	void delete(Long origItemId);
	Product get(Long origItemId);
	Product save(Product p) throws MissingDataException, DuplicateItemException;
	long count();
}
