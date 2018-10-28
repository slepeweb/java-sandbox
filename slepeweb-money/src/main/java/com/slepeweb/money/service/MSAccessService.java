package com.slepeweb.money.service;

import java.io.IOException;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.TimeWindow;
import com.slepeweb.money.bean.Transaction;

public interface MSAccessService {
	void init(Payee p, Category c, TimeWindow twin) throws IOException;
	void cacheAccount(Long l, Account a);
	void cachePayee(Long l, Payee a);
	void cacheCategory(Long l, Category c);
	Account getNextAccount() throws IOException;
	Payee getNextPayee() throws IOException;
	Category getNextCategory() throws IOException;
	Transaction getNextTransaction(TimeWindow twin) throws IOException;
	Transaction getNextSplitTransactions() throws IOException;
	Long[] getNextTransfer() throws IOException;
}
