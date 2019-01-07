package com.slepeweb.money.service;

import java.sql.Timestamp;
import java.util.List;

import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.solr.SolrResponse;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface TransactionService {
	Transaction get(long id);
	Transaction getByOrigId(long id);
	List<Transaction> getTransactionsForAccount(long id);
	List<Transaction> getTransactionsForAccount(long id, Timestamp from, Timestamp to);
	List<FlatTransaction> getTransactionsForCategory(long categoryId, int limit);
	List<FlatTransaction> getTransactionsForPayee(long payeeId, int limit);
	Timestamp getTransactionDateForAccount(long accountId, boolean first);
	Transaction save(Transaction p) throws MissingDataException, DuplicateItemException;
	Transaction update(Transaction from, Transaction to) throws MissingDataException, DuplicateItemException;
	void updateTransfer(Long from, Long to) throws MissingDataException, DuplicateItemException;
	void updateSplit(Transaction t);
	long getBalance(long accountId);
	long getBalance(long accountId, Timestamp to);
	SolrResponse<FlatTransaction> getTransactionsForPayee(long id);
	SolrResponse<FlatTransaction> getTransactionsForCategory(long id);
	long getNumTransactionsForAccount(long accountId);
	long getNumTransactionsForPayee(long payeeId);
	long getNumTransactionsForCategory(long categoryId);
}
