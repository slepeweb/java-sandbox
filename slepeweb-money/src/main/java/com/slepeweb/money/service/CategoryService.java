package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.Category;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface CategoryService {
	Category get(String major, String minor);
	Category get(long id);
	List<Category> getAll();
	Category save(Category p) throws MissingDataException, DuplicateItemException;
}