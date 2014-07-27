package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Field;


public interface FieldService {
//	void insertField(Field s);
//	void updateField(Field s);
	void deleteField(Long id);
	void deleteField(Field s);
	Field getField(String name);
	Field getField(Long id);
	Field save(Field f);
}
