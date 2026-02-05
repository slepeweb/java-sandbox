package com.slepeweb.money.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface TransactionService {
	Transaction get(long id);
	Transaction getByOrigId(int source, long id);
	Transaction getLastTransactionsForPayee(long payeeId);
	List<Transaction> getAll();
	List<Transaction> getTransactionsForAccount(long id);
	List<Transaction> getTransactionsForAccount(long id, Timestamp from, Timestamp to);
	List<Transaction> getUnreconciled(long accountId);
	List<Transaction> getTransactionsForPayee(long id);
	List<Transaction> getTransactionsForCategory(long id);
	List<Transaction> getTransactionsByDate(Date from, Date to);
	Timestamp getTransactionDateForAccount(long accountId, boolean first);
	Transaction save(Transaction p) throws MissingDataException, DuplicateItemException, DataInconsistencyException;
	Transaction update(Transaction from, Transaction to) throws MissingDataException, DuplicateItemException;
	void updateTransfer(Long from, Long to) throws MissingDataException, DuplicateItemException;
	void updateSplit(Transaction t);
	void updateReconciled(long id);
	long calculateBalance(long accountId);
	long calculateBalance(long accountId, Timestamp to);
	long getNumTransactionsForAccount(long accountId);
	long getNumTransactionsForPayee(long payeeId);
	long getNumTransactionsForCategory(long categoryId);
	int delete(long id);
}
