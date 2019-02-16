package com.slepeweb.money.service.acorn;

import java.io.IOException;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.AcornCategory;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.Transaction;

public interface AcornAccessService {
	void init(Payee p, Payee q, AcornCategory c, Account johnDoeAccount) throws IOException;
	void cacheAccount(Long l, Account a);
	void cacheAcornCategory(Long l, AcornCategory c);
	Account getNextAccount() throws IOException;
	AcornCategory getNextAcornCategory() throws IOException;
	Transaction getNextTransaction() throws IOException;
	void resetAccountOrPayeeCursor();
}
