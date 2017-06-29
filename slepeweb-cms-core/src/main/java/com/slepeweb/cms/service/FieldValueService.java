package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.except.ResourceException;


public interface FieldValueService {
	int deleteFieldValues(Long itemId);
	List<FieldValue> getFieldValues(Long itemId);
	FieldValue getFieldValue(Long fieldId, Long itemId);
	void deleteFieldValue(Long fieldId, Long itemId);
	FieldValue save(FieldValue fv) throws ResourceException;
	int getCount();
	int getCount(Long itemId);
}
