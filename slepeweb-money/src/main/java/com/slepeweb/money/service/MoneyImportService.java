package com.slepeweb.money.service;

import java.io.IOException;

import com.slepeweb.money.bean.Transaction;

public interface MoneyImportService {
	void init() throws IOException;
	Transaction importTransaction();
	boolean importTransfer();
	Transaction importSplitTransactions();
	Transaction saveTransaction(Transaction pt);
	void updateTransaction(Transaction ftom, Transaction to);
	Transaction saveSplitTransactions(Transaction pt);
	Transaction getTransactionByOrigId(long id);
}
