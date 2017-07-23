package com.slepeweb.commerce.service;

import java.util.List;

import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.commerce.bean.AxisValueSelector;
import com.slepeweb.commerce.bean.Variant;

public interface VariantService {
	void delete(Variant v);
	void deleteMany(Long origItemId, Long alphaValueId, Long betaValueId);
	void deleteMany(Long origItemId);
	Variant get(Long origItemId, String qualifier);
	Variant get(Long origItemId, Long alphaValueId, Long betaValueId);
	List<Variant> getMany(Long origItemId, Long alphaValueId, Long betaValueId);
	List<Variant> getVariantsWithBetaAxis(Long origItemId, Long alphaAxisValueId);
	AxisValueSelector getAlphaAxisSelector(Long origItemId, Long alphaAxisId);
	Variant save(Variant v) throws ResourceException;
	long count();
	Long count(Long origId);
}
