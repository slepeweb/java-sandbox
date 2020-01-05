package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.FieldValueSet;
import com.slepeweb.cms.except.ResourceException;


public interface FieldValueService {
	void deleteFieldValue(Long fieldId, Long itemId, String language);
	int deleteFieldValues(Long fieldId, Long itemId);
	int deleteFieldValues(Long itemId);
	FieldValue getFieldValue(Long fieldId, Long itemId, String language);
	FieldValueSet getFieldValues(Long itemId);
	FieldValueSet getFieldValues(Long fieldId, Long itemId);
	FieldValue save(FieldValue fv) throws ResourceException;
	int getCount();
	int getCount(Long itemId);
}
