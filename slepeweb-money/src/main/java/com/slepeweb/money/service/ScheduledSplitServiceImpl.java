package com.slepeweb.money.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.ScheduledTransaction;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("scheduledSplitService")
public class ScheduledSplitServiceImpl extends BaseServiceImpl implements ScheduledSplitService {
	
	private static Logger LOG = Logger.getLogger(ScheduledSplitServiceImpl.class);
	private static final String SELECT = 
			"select " +
					"st.id, st.scheduledtransactionid, st.amount, st.memo, " + 
					"c.id as categoryid, c.major, c.minor " + 
			"from scheduledsplit st " +
					"join category c on c.id = st.categoryid ";
	
	public ScheduledTransaction save(ScheduledTransaction t) throws MissingDataException, DuplicateItemException {
		if (t.isSplit()) {
			List<SplitTransaction> revisedList = new ArrayList<SplitTransaction>(t.getSplits().size());
			revisedList.addAll(t.getSplits());
			
			/*
			 * Transaction t is fully defined, but the number and the content of its splits may have changed.
			 * So, first we'll delete the transaction's existing splits from the database.
			 */
			t = delete(t);
			
			// Insert latest splits
			for (SplitTransaction st : revisedList) {
				st.setTransactionId(t.getId());
				if (st.isDefined4Insert()) {
					t.getSplits().add(insert(st));
				}
				else {
					throw new MissingDataException(error(LOG, "Scheduled split transactions not saved - insufficient data", t));
				}
			}			
		}
		
		return t;
	}
	
	private SplitTransaction insert(SplitTransaction st) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into scheduledsplit (scheduledtransactionid, categoryid, amount, memo) " +
					"values (?, ?, ?, ?)", 
					st.getTransactionId(), st.getCategory().getId(), st.getAmount(), st.getMemo());
			
			LOG.info(compose("Added new split scheduled transaction", st));		
			st.setId(getLastInsertId());	
			return st;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Scheduled split transaction already inserted");
		}
	}

	public List<SplitTransaction> get(long id) {
		return this.jdbcTemplate.query(
				SELECT + " where st.scheduledtransactionid = ?", 
				new RowMapperUtil.ScheduledSplitMapper(), 
				id);
	}

	public ScheduledTransaction delete(ScheduledTransaction t) {
		if (this.jdbcTemplate.update("delete from scheduledsplit where scheduledtransactionid = ?", t.getId()) > 0) {
			LOG.warn(compose("Deleted scheduled split transactions", t.getId()));
		}
		
		t.getSplits().clear();
		return t;
	}	
}
