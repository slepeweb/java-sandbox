package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface SplitTransactionService {
	List<SplitTransaction> get(Transaction pt);
	Transaction save(Transaction pt) throws MissingDataException, DuplicateItemException;
	Transaction delete(Transaction pt);
}
