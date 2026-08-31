package com.slepeweb.money.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.NakedTransaction;
import com.slepeweb.money.bean.YearlyAssetHistory;
import com.slepeweb.money.bean.YearlyAssetStatus;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("assetService")
public class AssetServiceImpl extends BaseServiceImpl implements AssetService {
	
	private static Logger LOG = Logger.getLogger(AssetServiceImpl.class);
	
	public void save(YearlyAssetHistory yah) throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		List<Integer> storedYears = getYears(getAll());
		List<Integer> latestYears = getYears(yah.getList());
		Iterator<Integer> iter = storedYears.iterator();
		Integer i;
		
		// Identify records that need to be deleted
		while (iter.hasNext()) {
			i = iter.next();
			if (latestYears.contains(i)) {
				iter.remove();
			}
		}
		
		// What has remained in storedYears needs to be deleted
		for (Integer year : storedYears) {
			delete(year);
		}
		
		// Now save ALL the records provided
		for (YearlyAssetStatus yearlySummary : yah.getList()) {
			save(yearlySummary);
		}
	}
	
	private List<Integer> getYears(List<YearlyAssetStatus> summaries) {
		List<Integer> list = new ArrayList<Integer>();
		for (YearlyAssetStatus yas : summaries) {
			list.add(yas.getYear());
		}
		return list;
	}
	
	public YearlyAssetStatus save(YearlyAssetStatus yas) throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		if (yas.isDefined4Insert()) {
			YearlyAssetStatus dbRecord = get(yas.getYear());		
			if (dbRecord != null) {
				return update(dbRecord, yas);
			}
			else {
				return insert(yas);
			}
		}
		else {
			throw new MissingDataException(error(LOG, "YearlyAssetStatus not saved - insufficient data", yas));
		}
	}
	
	private YearlyAssetStatus insert(YearlyAssetStatus yas) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into assethistory (year, income, expense, balance) values (?, ?, ?, ?)", 
					yas.getYear(), yas.getIncome(), yas.getExpense(), yas.getBalance());
			
			LOG.info(compose("Added new YearlyAssetStatus", yas));
			return yas;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("YearlyAssetStatus already inserted");
		}
	}

	public YearlyAssetStatus update(YearlyAssetStatus dbRecord, YearlyAssetStatus yas) {
		if (! dbRecord.equals(yas)) {
			dbRecord.assimilate(yas);
			
			this.jdbcTemplate.update(
					"update assethistory set income = ?, expense = ?, balance = ? where year = ?", 
					dbRecord.getIncome(), dbRecord.getExpense(), dbRecord.getBalance(), dbRecord.getYear());
			
			LOG.info(compose("Updated YearlyAssetStatus", yas));
		}
		else {
			LOG.debug(compose("YearlyAssetStatus not modified", yas));
		}
		
		return dbRecord;
	}
	
	public YearlyAssetStatus get(int year) {
		try {
			return this.jdbcTemplate.queryForObject(
				"select * from assethistory where year = ?", new RowMapperUtil.YearlyAssetStatusMapper(), new Object[] {year});
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<YearlyAssetStatus> getAll() {
			return this.jdbcTemplate.query(
				"select * from assethistory order by year", new RowMapperUtil.YearlyAssetStatusMapper());
	}

	/*
	 * WARNING: This method returns partially-populated Transaction objects.
	 * Classes that call this method must be aware. The populated properties are:
	 * 		entered
	 * 		amount
	 * 		transferId
	 * 		memo (hack: using memo property to store the category type
	 */
	public List<NakedTransaction> getTransactionsBetween(Date from, Date to) {
		return this.jdbcTemplate.query(
				"select t.entered, t.amount, t.transferid, c.expense " + 
				"from transaction t, account a, category c " + 
				"where " +
				"t.accountid = a.id and " +
				"t.categoryid = c.id and " +
				"a.type != 'other' and " +
				"t.entered >= ? and " +
				"t.entered <= ? " +
				"order by t.entered", 
				new RowMapperUtil.NakedTransactionMapper(),
				new Object[]{from, to});
	}

	public boolean delete(Integer year) {
		LOG.info(String.format("Deleting asset history record [%d] ...", year));
		return this.jdbcTemplate.update("delete from assethistory where year = ?", year) > 0;
	}
}
