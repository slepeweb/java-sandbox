package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Field;


public interface FieldService {
	void deleteField(Field s);
	Field getField(String name);
	Field getField(Long id);
	Field save(Field f);
	int getCount();
}
