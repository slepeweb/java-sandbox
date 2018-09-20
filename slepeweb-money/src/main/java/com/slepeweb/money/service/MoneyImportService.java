package com.slepeweb.money.service;

import java.io.IOException;

import com.slepeweb.money.bean.Transaction;

public interface MoneyImportService {
	void init() throws IOException;
	Transaction importTransaction();
	Long[] importTransfer();
	Transaction importSplitTransactions();
	Transaction saveTransaction(Transaction pt);
	void updateTransaction(Transaction ftom, Transaction to);
	void updateTransfer(Long from, Long to);
	Transaction saveSplitTransactions(Transaction pt);
	Transaction getTransactionByOrigId(long id);
}
