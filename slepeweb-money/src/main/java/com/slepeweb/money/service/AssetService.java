package com.slepeweb.money.service;

import java.sql.Date;
import java.util.List;

import com.slepeweb.money.bean.NakedTransaction;
import com.slepeweb.money.bean.YearlyAssetHistory;
import com.slepeweb.money.bean.YearlyAssetStatus;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;


public interface AssetService {
	YearlyAssetStatus save(YearlyAssetStatus yas) throws MissingDataException, DuplicateItemException, DataInconsistencyException;
	void save(YearlyAssetHistory yah) throws MissingDataException, DuplicateItemException, DataInconsistencyException;
	YearlyAssetStatus get(int year);
	List<YearlyAssetStatus> getAll();
	List<NakedTransaction> getTransactionsBetween(Date from, Date to);
}