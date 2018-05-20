package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface AccountService {
	Account get(String name);
	Account get(long id);
	List<Account> getAll();
	Account save(Account f) throws MissingDataException, DuplicateItemException;
	void updateBalance(Account a);
	Account resetBalance(Account a);
}
