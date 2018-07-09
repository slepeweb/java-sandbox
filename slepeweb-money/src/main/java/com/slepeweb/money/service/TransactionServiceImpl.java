package com.slepeweb.money.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("transactionService")
public class TransactionServiceImpl extends BaseServiceImpl implements TransactionService {
	
	private static Logger LOG = Logger.getLogger(TransactionServiceImpl.class);
	
	private static final String SELECT = 
			"select " +
					"a.id as accountid, a.name as accountname, " + 
					"p.id as payeeid, p.name as payeename, " + 
					"c.id as categoryid, c.major, c.minor, " + 
					"t.id, t.origid, t.entered, t.memo, t.reference, t.amount, t.reconciled, " +
					"t.transferid " +
			"from transaction t " +
					"join account a on a.id = t.accountid " + 
					"join payee p on p.id = t.payeeid " +
					"join category c on c.id = t.categoryid ";
	
	public Transaction save(Transaction pt) throws MissingDataException, DuplicateItemException {
		if (pt.isDefined4Insert()) {
			// Insert record, regardless of whether it has already been inserted.
			// (Take care with imports - should check whether already imported first!)
			insert(pt);
		}
		else {
			String t = "Transaction not saved - insufficient data";
			LOG.error(compose(t, pt));
			throw new MissingDataException(t);
		}
		
		return pt;
	}
	
	private Transaction insert(Transaction t) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into transaction (accountid, payeeid, categoryid, origid, entered, amount, " +
					"reconciled, transferid, reference, memo) " +
					"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
					t.getAccount().getId(), t.getPayee().getId(), t.getCategory().getId(), 
					t.getOrigId(), t.getEntered(), t.getAmount(),
					t.isReconciled(), t.getTransferId(), t.getReference(), t.getMemo());
			
			t.setId(getLastInsertId());	
			LOG.info(compose("Added new transaction", t));		
			return t;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Transaction already inserted");
		}
	}

	public void update(Transaction dbRecord, Transaction t) {
		if (! dbRecord.equalsBarTransferId(t)) {
			dbRecord.assimilate(t);
			
			try {
				this.jdbcTemplate.update(
						"update transaction set entered = ?, " + 
						"accountid = ?, payeeid = ?, categoryid = ?, " + 
						"amount = ?, reconciled = ?, " +
						"memo = ?, reference = ? " +
						"where id = ?", 
						dbRecord.getEntered(), 
						dbRecord.getAccount().getId(), dbRecord.getPayee().getId(), dbRecord.getCategory().getId(), 
						dbRecord.getAmount(), dbRecord.isReconciled(), 
						dbRecord.getMemo(), dbRecord.getReference(),
						dbRecord.getId());
				
				LOG.info(compose("Updated transaction", t));
			}
			catch (DuplicateKeyException e) {
				LOG.error(compose("Duplicate key", t));
			}
		}
		else {
			LOG.debug(compose("Transaction not modified", t));
		}
	}

	public void updateTransfer(Long from, Long to) {
		this.jdbcTemplate.update(
				"update transaction set transferid = ? where id = ?", 
				to, from);
		
		this.jdbcTemplate.update(
				"update transaction set transferid = ? where id = ?", 
				from, to);
		
		LOG.info(compose("Updated transfer details", from, to));
	}

	public Transaction get(long id) {
		return (Transaction) getFirstInList(this.jdbcTemplate.query(
				SELECT + "where t.id = ?", 
				new Object[]{id}, 
				new RowMapperUtil.TransactionMapper()));
	}

	public Transaction getByOrigId(long origId) {
		return (Transaction) getFirstInList(this.jdbcTemplate.query(
				SELECT + "where t.origid = ?", 
				new Object[]{origId}, 
				new RowMapperUtil.TransactionMapper()));
	}

	public List<Transaction> getTransactionsForAccount(long accountId) {
		return this.jdbcTemplate.query(
				SELECT + "where t.accountid = ? order by t.entered", 
				new Object[]{accountId}, 
				new RowMapperUtil.TransactionMapper());
	}
	
	public List<Transaction> getTransactionsForAccount(long accountId, Timestamp from, Timestamp to) {
		return this.jdbcTemplate.query(
				SELECT + "where t.accountid = ? and t.entered >= ? and t.entered <= ? order by t.entered", 
				new Object[]{accountId, from, to}, 
				new RowMapperUtil.TransactionMapper());
	}
	
}
