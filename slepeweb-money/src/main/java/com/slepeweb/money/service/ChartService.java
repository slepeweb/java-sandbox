package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.Chart;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface ChartService {
	Chart get(long id);
	List<Chart> getAll();
	Chart save(Chart ch) throws MissingDataException, DuplicateItemException, DataInconsistencyException;
	int delete(long id);
}
