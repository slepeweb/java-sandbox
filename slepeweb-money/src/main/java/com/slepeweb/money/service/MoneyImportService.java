package com.slepeweb.money.service;

import java.io.IOException;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Transaction;

public interface MoneyImportService {
	void init() throws IOException;
	Account getAccount(String account);
	Transaction importTransaction();
	boolean importTransfer();
	boolean importSplit();
	Transaction saveTransaction(Transaction pt);
	Transaction saveSplitTransactions(Transaction pt);
	Account resetAccountBalance(Account a);
	Transaction getTransactionByOrigId(long id);
}
