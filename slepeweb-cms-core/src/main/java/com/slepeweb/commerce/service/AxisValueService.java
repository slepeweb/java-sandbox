package com.slepeweb.commerce.service;

import java.util.List;

import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.commerce.bean.AxisValue;

public interface AxisValueService {
	void delete(AxisValue a);
	void delete(Long id);
	AxisValue get(Long id);
	List<AxisValue> getAll(Long axisId);
	AxisValue save(AxisValue a) throws ResourceException;
}
