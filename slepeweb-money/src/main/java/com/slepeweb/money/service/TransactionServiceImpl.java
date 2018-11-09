package com.slepeweb.money.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("transactionService")
public class TransactionServiceImpl extends BaseServiceImpl implements TransactionService {
	
	private static Logger LOG = Logger.getLogger(TransactionServiceImpl.class);
	@Autowired private AccountService accountService;
	@Autowired private SplitTransactionService splitTransactionService;
	
	private static final String FROM = 
			"from transaction t " +
			"join account a on a.id = t.accountid " + 
			"join payee p on p.id = t.payeeid " +
			"join category c on c.id = t.categoryid ";
	
	private static final String SELECT = 
			"select " +
					"a.id as accountid, a.name as accountname, a.openingbalance, a.closed, a.note, " + 
					"p.id as payeeid, p.name as payeename, " + 
					"c.id as categoryid, c.major, c.minor, " + 
					"t.id, t.origid, t.entered, t.memo, t.reference, t.amount, t.reconciled, " +
					"t.transferid, t.split " + FROM;
	
	private static final String FLAT_SELECT = 
			"select " +
					"a.name as account, " + 
					"p.name as payee, " + 
					"c.major, c.minor, " + 
					"t.entered, t.memo, t.reference, t.amount " + FROM;
			;
	
	public Transaction save(Transaction pt) throws MissingDataException, DuplicateItemException {
		if (pt.isDefined4Insert()) {
			// Insert record, regardless of whether it has already been inserted.
			// (Take care with imports - should check whether already imported first!)
			return insert(pt);
		}
		else {
			String t = "Transaction not saved - insufficient data";
			LOG.error(compose(t, pt));
			throw new MissingDataException(t);
		}
	}
	
	private Transaction insert(Transaction t) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into transaction (accountid, payeeid, categoryid, split, origid, entered, amount, " +
					"reconciled, transferid, reference, memo) " +
					"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
					t.getAccount().getId(), t.getPayee().getId(), t.getCategory().getId(), 
					t.isSplit(), t.getOrigId(), t.getEntered(), t.getAmount(),
					t.isReconciled(), t.getTransferId(), t.getReference(), t.getMemo());
			
			t.setId(getLastInsertId());	
			LOG.info(compose("Added new transaction", t));		
			return t;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Transaction already inserted");
		}
	}

	public Transaction update(Transaction dbRecord, Transaction t) {
		if (! dbRecord.equalsBarTransferId(t)) {
			dbRecord.assimilate(t);
			
			try {
				this.jdbcTemplate.update(
						"update transaction set entered = ?, " + 
						"accountid = ?, payeeid = ?, categoryid = ?, " + 
						"split = ?, amount = ?, reconciled = ?, " +
						"memo = ?, reference = ? " +
						"where id = ?", 
						dbRecord.getEntered(), 
						dbRecord.getAccount().getId(), dbRecord.getPayee().getId(), dbRecord.getCategory().getId(), 
						dbRecord.isSplit(), dbRecord.getAmount(), dbRecord.isReconciled(), 
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
		
		return dbRecord;
	}

	public void updateSplit(Transaction t) {
		this.jdbcTemplate.update(
				"update transaction set split = ? where id = ?", 
				t.isSplit(), t.getId());
		
		LOG.info(compose("Updated split flag for transaction", t));
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
		return get(SELECT + "where t.id = ?", new Object[]{id});
	}

	public Transaction getByOrigId(long origId) {
		return get(SELECT + "where t.origid = ?", new Object[]{origId});
	}
	
	private Transaction get(String sql, Object[] params) {
		Transaction t = (Transaction) getFirstInList(this.jdbcTemplate.query(
				sql, params, new RowMapperUtil.TransactionMapper()));
		
		if (t != null && t.isSplit()) {
			t.setSplits(this.splitTransactionService.get(t));
		}
		
		return t;
	}

	public Timestamp getTransactionDateForAccount(long accountId, boolean first) {
		String sql = String.format("select entered from transaction where accountid = ? order by entered %s limit 1", 
				first ? "" : "desc");
		
		List<Timestamp> list = this.jdbcTemplate.query(
				sql,
				new Object[]{accountId}, new RowMapperUtil.TransactionDateMapper());
		
		if (list.size() == 1) {
			return list.get(0);
		}
		
		return first ? new Timestamp(0L) : new Timestamp(new Date().getTime());
	}
	
	public List<Transaction> getTransactionsForAccount(long accountId) {
		return getTransactionsForAccount(
				SELECT + "where t.accountid = ? order by t.entered", 
				new Object[]{accountId});
	}
	
	public List<Transaction> getTransactionsForAccount(long accountId, Timestamp from, Timestamp to) {
		return getTransactionsForAccount(
				SELECT + "where t.accountid = ? and t.entered >= ? and t.entered <= ? order by t.entered", 
				new Object[]{accountId, from, to});
	}
	
	private List<Transaction> getTransactionsForAccount(String sql, Object[] params) {
		List<Transaction> list = this.jdbcTemplate.query(
				sql, params, new RowMapperUtil.TransactionMapper());
		
		for (Transaction t : list) {
			if (t.isSplit()) {
				t.setSplits(this.splitTransactionService.get(t));
			}
		}
		
		return list;
	}
	
	public List<FlatTransaction> getTransactionsForCategory(long categoryId) {
		return this.jdbcTemplate.query(
				FLAT_SELECT + "where t.categoryid = ? order by t.entered desc", 
				new Object[]{categoryId},
				new RowMapperUtil.FlatTransactionMapper());
	}
	
	public long getBalance(long accountId) {
		return getBalance(accountId, null);
	}
	
	@SuppressWarnings("deprecation")
	public long getBalance(long accountId, Timestamp to) {
		StringBuilder sb = new StringBuilder("select sum(amount) from transaction where accountid = ? ");
		int arrlen = 1 + (to != null ? 1 : 0);
		Object[] params = new Object[arrlen];
		int index = 0;
		params[index++] = accountId;
		
		if (to != null) {
			sb.append("and entered <= ? ");
			params[index++] = to;
		}		
		
		long sum = this.jdbcTemplate.queryForLong(sb.toString(), params);
		Account a = this.accountService.get(accountId);
		return a.getOpeningBalance() + sum;
	}
}
