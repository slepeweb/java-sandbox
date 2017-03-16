package com.slepeweb.funds.service;

import java.util.List;

import com.slepeweb.funds.bean.Fund;
import com.slepeweb.funds.except.DuplicateItemException;
import com.slepeweb.funds.except.MissingDataException;


public interface FundService {
	Fund getFund(String name);
	Fund getFund(long id);
	List<Fund> getAllFunds();
	Fund save(Fund f) throws MissingDataException, DuplicateItemException;
}
