package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.FieldValue;


public interface FieldValueService {
	void insertFieldValue(FieldValue s);
	void updateFieldValue(FieldValue s);
	FieldValue getFieldValue(Long fieldId, Long itemId);
	void deleteFieldValue(Long fieldId, Long itemId);
}
