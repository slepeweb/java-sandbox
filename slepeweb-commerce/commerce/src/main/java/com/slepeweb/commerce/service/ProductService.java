package com.slepeweb.commerce.service;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.commerce.bean.Product;

public interface ProductService {
	void deleteProduct(Long origItemId);
	Product getProduct(Long origItemId);
	Product save(Product p) throws MissingDataException, DuplicateItemException;
}
