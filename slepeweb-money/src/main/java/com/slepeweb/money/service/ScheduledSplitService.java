package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.bean.ScheduledTransaction;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface ScheduledSplitService {
	List<SplitTransaction> get(long id);
	ScheduledTransaction save(ScheduledTransaction pt) throws MissingDataException, DuplicateItemException;
	ScheduledTransaction delete(ScheduledTransaction pt);
}
