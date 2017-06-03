package com.slepeweb.commerce.service;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.commerce.bean.Axis;

public interface AxisService {
	void delete(Axis a);
	void delete(Long id);
	Axis get(Long id);
	Axis save(Axis a) throws MissingDataException, DuplicateItemException;
}
