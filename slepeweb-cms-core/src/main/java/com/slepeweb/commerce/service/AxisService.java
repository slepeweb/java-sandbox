package com.slepeweb.commerce.service;

import java.util.List;

import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.commerce.bean.Axis;

public interface AxisService {
	void delete(Axis a);
	void delete(Long id);
	Axis get(Long id);
	Axis get(String shortname);
	List<Axis> get();
	Axis save(Axis a) throws ResourceException;
}
