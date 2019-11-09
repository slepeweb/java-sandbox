package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.Property;
import com.slepeweb.money.except.DuplicateItemException;


public interface PropertyService {
	Property get(String name);
	List<Property> getAll();
	Property save(Property f) throws DuplicateItemException;
	int delete(String name);
}