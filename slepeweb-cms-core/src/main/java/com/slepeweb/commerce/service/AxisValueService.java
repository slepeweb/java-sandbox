package com.slepeweb.commerce.service;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.commerce.bean.AxisValue;

public interface AxisValueService {
	void delete(AxisValue a);
	void delete(Long id);
	AxisValue get(Long id);
	AxisValue save(AxisValue a) throws MissingDataException, DuplicateItemException;
}
