package com.slepeweb.commerce.service;

import java.util.List;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.commerce.bean.Variant;

public interface VariantService {
	void deleteVariant(Variant v);
	void deleteVariant(Long origItemId, Long alphaValueId, Long betaValueId);
	void deleteVariants(Long origItemId);
	Variant getVariant(Long origItemId, Long alphaValueId, Long betaValueId);
	List<Variant> getVariants(Long origItemId, Long alphaValueId, Long betaValueId);
	Variant save(Variant v) throws MissingDataException, DuplicateItemException;
}
