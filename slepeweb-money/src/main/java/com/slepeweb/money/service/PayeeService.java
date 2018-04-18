package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface PayeeService {
	Payee get(String name);
	Payee get(long id);
	List<Payee> getAll();
	Payee save(Payee p) throws MissingDataException, DuplicateItemException;
}
