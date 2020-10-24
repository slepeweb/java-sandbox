package com.slepeweb.money.service;

import java.io.IOException;

import com.slepeweb.money.bean.TimeWindow;
import com.slepeweb.money.bean.Transaction;

public interface MoneyImportService {
	void init(String mdbFilePath, TimeWindow twin) throws IOException;
	Transaction importTransaction(TimeWindow twin);
	Long[] importTransfer();
	Transaction importSplitTransactionsParentId();
	void populateSplitTransactions(Transaction t);
	Transaction saveTransaction(Transaction pt);
	Transaction updateTransaction(Transaction ftom, Transaction to);
	void updateTransfer(Long from, Long to);
	Transaction saveSplitTransactions(Transaction pt);
	Transaction getTransactionByOrigId(long id);
}
