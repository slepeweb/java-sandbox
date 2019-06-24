package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.ScheduledTransaction;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface ScheduledTransactionService {
	ScheduledTransaction get(long id);
	List<ScheduledTransaction> getAll();
	ScheduledTransaction save(ScheduledTransaction p) throws MissingDataException, DuplicateItemException, DataInconsistencyException;
	ScheduledTransaction update(ScheduledTransaction from, ScheduledTransaction to) throws MissingDataException, DuplicateItemException;
	void updateSplit(ScheduledTransaction t);
	int delete(long id);
}
