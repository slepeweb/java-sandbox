package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.FieldForType;


public interface FieldForTypeService {
	void insertFieldForType(FieldForType s);
	void updateFieldForType(FieldForType s);
	FieldForType getFieldForType(Long fieldId, Long itemTypeId);
	void deleteFieldForType(Long fieldId, Long itemTypeId);
}
