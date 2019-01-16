package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.Category;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface CategoryService {
	Category get(String major, String minor);
	Category getNoCategory();
	Category get(long id);
	Category getByOrigId(long id);
	List<Category> getAll();
	Category save(Category p) throws MissingDataException, DuplicateItemException, DataInconsistencyException;
	Category update(Category existing, Category with);
	List<String> getAllMajorValues();
	List<String> getAllMinorValues(String major);
	int delete(long id);
}
