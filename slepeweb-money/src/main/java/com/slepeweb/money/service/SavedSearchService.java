package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface SavedSearchService {
	SavedSearch get(long id);
	List<SavedSearch> getAll();
	SavedSearch save(SavedSearch ss) throws MissingDataException, DuplicateItemException, DataInconsistencyException;
	int delete(long id);
}
