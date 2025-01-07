package com.slepeweb.money.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.ScheduledTransaction;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("scheduledTransactionService")
public class ScheduledTransactionServiceImpl extends BaseServiceImpl implements ScheduledTransactionService {
	
	private static Logger LOG = Logger.getLogger(ScheduledTransactionServiceImpl.class);
	@Autowired private ScheduledSplitService scheduledSplitService;
	
	private static final String FROM = 
			"from scheduledtransaction t " +
				"join account a on a.id = t.accountid " + 
				"join payee p on p.id = t.payeeid " +
				"join category c on c.id = t.categoryid " +
				"left join account m on m.id = t.mirrorid ";
	
	private static final String SELECT = 
			"select " +
					"a.id as accountid, a.name as accountname, " +
					"m.id as mirrorid, m.name as mirrorname, " +
					"p.id as payeeid, p.name as payeename, " +
					"c.id as categoryid, c.major, c.minor, " +
					"t.id, t.label, t.dayofmonth, " +
					"t.lastentered, t.memo, t.reference, t.amount, " +
					"t.split " + FROM;
	
	public ScheduledTransaction save(ScheduledTransaction pt) 
			throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		
		if (pt.isDefined4Insert()) {
			ScheduledTransaction t;
			
			if (pt.isInDatabase()) {
				ScheduledTransaction dbRecord = get(pt.getId());	
				if (dbRecord != null) {
					t = update(dbRecord, pt);
				}
				else {
					throw new DataInconsistencyException(error(LOG, "ScheduledTransaction does not exist in DB", pt));
				}
			}
			else {
				t = insert(pt);
			}
			
			// Also save this transaction's splits, if any
			t = this.scheduledSplitService.save(t);
			
			return t;
		}
		else {
			String t = "ScheduledTransaction not saved - insufficient data";
			LOG.error(compose(t, pt));
			throw new MissingDataException(t);
		}
	}
	
	private ScheduledTransaction insert(ScheduledTransaction scht) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into scheduledtransaction (label, dayofmonth, lastentered, accountid, mirrorid, payeeid, " +
					"categoryid, split, amount, reference, memo) " +
					"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
					scht.getLabel(), scht.getDay(), scht.getEntered(),
					scht.getAccount().getId(), 
					scht.getMirror() != null ? scht.getMirror().getId() : null, 
					scht.getPayee().getId(), scht.getCategory().getId(), 
					scht.isSplit(), scht.getAmount(),
					scht.getReference(), scht.getMemo());
			
			scht.setId(getLastInsertId());	
			LOG.info(compose("Added new scheduled transaction", scht));		
			return scht;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("ScheduledTransaction already inserted");
		}
	}

	public ScheduledTransaction update(ScheduledTransaction dbRecord, ScheduledTransaction t) {
		if (! dbRecord.equals(t)) {
			dbRecord.assimilate(t);
			
			try {
				this.jdbcTemplate.update(
						"update scheduledtransaction set label = ?, dayofmonth = ?, " + 
						"accountid = ?, mirrorid = ?, payeeid = ?, categoryid = ?,  " + 
						"split = ?, amount = ?, " +
						"memo = ?, reference = ? " +
						"where id = ?", 
						dbRecord.getLabel(), dbRecord.getDay(),
						dbRecord.getAccount().getId(), 
						dbRecord.getMirror() != null ? dbRecord.getMirror().getId() : null, 
						dbRecord.getPayee().getId(), dbRecord.getCategory().getId(),
						dbRecord.isSplit(), dbRecord.getAmount(),  
						dbRecord.getMemo(), dbRecord.getReference(), 
						dbRecord.getId());
				
				LOG.info(compose("Updated scheduled transaction", dbRecord));
			}
			catch (DuplicateKeyException e) {
				LOG.error(compose("Duplicate key", dbRecord));
			}
		}
		else {
			LOG.debug(compose("ScheduledTransaction not modified", dbRecord));
		}
		
		return dbRecord;
	}

	public void updateSplit(ScheduledTransaction t) {
		this.jdbcTemplate.update(
				"update scheduledtransaction set split = ? where id = ?", 
				t.isSplit(), t.getId());
		
		LOG.info(compose("Updated split flag for transaction", t));
	}

	public ScheduledTransaction get(long id) {
		return get(SELECT + "where t.id = ?", id);
	}

	private ScheduledTransaction get(String sql, Object... params) {
		try {
			ScheduledTransaction t = this.jdbcTemplate.queryForObject(
					sql, new RowMapperUtil.ScheduledTransactionMapper(), params);
			
			if (t != null && t.isSplit()) {
				t.setSplits(this.scheduledSplitService);
			}
	
			return t;
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<ScheduledTransaction> getAll() {
		String sql = SELECT + "order by t.label";
		List<ScheduledTransaction> all = this.jdbcTemplate.query(
				sql, new RowMapperUtil.ScheduledTransactionMapper());
		
		for (ScheduledTransaction scht : all) {
			if (scht.isSplit()) {
				scht.setSplits(this.scheduledSplitService);
			}
		}
		
		return all;
	}
	
	public int delete(long id) {
		return this.jdbcTemplate.update("delete from scheduledtransaction where id = ?", id);		
	}	
	
	public void updateLastEntered(ScheduledTransaction t) {
		this.jdbcTemplate.update("update scheduledtransaction set lastentered = ? where id = ?", 
				t.getEntered(), t.getId());
		LOG.info(compose("Updated lastentered for scheduled transaction", t));
	}

}
