package com.slepeweb.commerce.service;

import java.util.List;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.commerce.bean.Variant;

public interface VariantService {
	void delete(Variant v);
	void deleteMany(Long origItemId, Long alphaValueId, Long betaValueId);
	void deleteMany(Long origItemId);
	Variant get(String sku);
	List<Variant> getMany(Long origItemId, Long alphaValueId, Long betaValueId);
	Variant save(Variant v) throws MissingDataException, DuplicateItemException;
	long count();
}
