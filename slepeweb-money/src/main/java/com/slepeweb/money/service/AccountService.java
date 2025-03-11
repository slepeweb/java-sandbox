package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface AccountService {
	Account get(String name);
	Account get(long id);
	Account getByOrigId(long id);
	List<Account> getAll();
	List<Account> getAll(boolean includingClosed);
	List<Account> getAllWithBalances();
	List<Account> getAssets();
	Account save(Account f) throws MissingDataException, DuplicateItemException, DataInconsistencyException;
	Account update(Account existing, Account with);
	void updateReconciled(Account a);
	int delete(long id);
}