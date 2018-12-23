package com.slepeweb.money.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("splitTransactionService")
public class SplitTransactionServiceImpl extends BaseServiceImpl implements SplitTransactionService {
	
	private static Logger LOG = Logger.getLogger(SplitTransactionServiceImpl.class);
	private static final String SELECT = 
			"select " +
					"st.id, st.transactionid, st.amount, st.memo, " + 
					"c.id as categoryid, c.origid as categoryorigid, c.major, c.minor " + 
			"from splittransaction st " +
					"join category c on c.id = st.categoryid ";
	public Transaction save(Transaction t) throws MissingDataException, DuplicateItemException {
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
				if (st.isDefined4Insert()) {
					t.getSplits().add(insert(st));
				}
				else {
					throw new MissingDataException(error(LOG, "Split transactions not saved - insufficient data", t));
				}
			}			
		}
		
		return t;
	}
	
	private SplitTransaction insert(SplitTransaction st) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into splittransaction (transactionid, categoryid, amount, memo) " +
					"values (?, ?, ?, ?)", 
					st.getTransactionId(), st.getCategory().getId(), st.getAmount(), st.getMemo());
			
			LOG.info(compose("Added new split transaction", st));		
			return st;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Split transaction already inserted");
		}
	}

	public List<SplitTransaction> get(Transaction t) {
		return this.jdbcTemplate.query(
				SELECT + " where st.transactionid = ?", 
				new Object[]{t.getId()}, 
				new RowMapperUtil.SplitTransactionMapper());
	}

	public Transaction delete(Transaction t) {
		if (this.jdbcTemplate.update("delete from splittransaction where transactionid = ?", t.getId()) > 0) {
			LOG.warn(compose("Deleted split transactions", t.getId()));
		}
		
		t.getSplits().clear();
		return t;
	}	
}
