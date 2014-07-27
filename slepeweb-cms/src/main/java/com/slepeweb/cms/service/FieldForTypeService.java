package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.FieldForType;


public interface FieldForTypeService {
	FieldForType getFieldForType(Long fieldId, Long itemTypeId);
	void deleteFieldForType(Long fieldId, Long itemTypeId);
	List<FieldForType> getFieldsForType(Long itemTypeId);
	FieldForType save(FieldForType fft);
}
