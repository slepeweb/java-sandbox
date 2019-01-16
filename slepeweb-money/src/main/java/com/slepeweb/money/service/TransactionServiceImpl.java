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
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("transactionService")
public class TransactionServiceImpl extends BaseServiceImpl implements TransactionService {
	
	private static Logger LOG = Logger.getLogger(TransactionServiceImpl.class);
	@Autowired private AccountService accountService;
	@Autowired private SplitTransactionService splitTransactionService;
	@Autowired private SolrService solrService;
	
	private static final String FROM = 
			"from transaction t " +
			"join account a on a.id = t.accountid " + 
			"join payee p on p.id = t.payeeid " +
			"join category c on c.id = t.categoryid ";
	
	private static final String SELECT = 
			"select " +
					"a.id as accountid, a.origid as accountorigid, a.name as accountname, " +
					"a.type as accounttype, a.openingbalance, a.closed, a.note, " + 
					"p.id as payeeid, p.origid as payeeorigid, p.name as payeename, " + 
					"c.id as categoryid, c.origid as categoryorigid, c.major, c.minor, " + 
					"t.id, t.origid, t.entered, t.memo, t.reference, t.amount, t.reconciled, " +
					"t.transferid, t.split " + FROM;
	
	private static final String FLAT_SELECT = 
			"select " +
					"a.name as account, " + 
					"p.name as payee, " + 
					"c.major, c.minor, " + 
					"t.id, t.entered as entered, t.memo, t.reference, t.amount " + FROM;
			;
	
	private static final String FLAT_SELECT_CATEGORY = 
			"(" + FLAT_SELECT +
			"where c.id = ?) " +
			"union " +
			"(select " +
					"a.name as account, " + 
					"p.name as payee, " + 
					"c.major, c.minor, " + 
					"t.id, t.entered as entered, st.memo, t.reference, st.amount " + 
			"from splittransaction st " +
					"join transaction t on t.id = st.transactionid " + 
					"join account a on a.id = t.accountid " + 
					"join payee p on p.id = t.payeeid " +
					"join category c on c.id = st.categoryid " +
			"where c.id = ?) " +
			"order by entered desc"
			;
	
			
	public Transaction save(Transaction pt) throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		if (pt.isDefined4Insert()) {
			Transaction result;
			
			if (pt.isInDatabase()) {
				Transaction dbRecord = get(pt.getId());		
				if (dbRecord != null) {
					result = update(dbRecord, pt);
				}
				else {
					throw new DataInconsistencyException(error(LOG, "Transaction does not exist in DB", pt));
				}
			}
			else {
				result = insert(pt);
			}
			
			result = this.splitTransactionService.save(result);
			this.solrService.save(result);
			return result;
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
		
		this.solrService.save(dbRecord);
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
	
	@SuppressWarnings("deprecation")
	public long getNumTransactionsForAccount(long accountId) {
		return this.jdbcTemplate.queryForLong(
				"select count(*) from transaction where accountid = ?", 
				new Object[]{accountId});
	}
	
	@SuppressWarnings("deprecation")
	public long getNumTransactionsForPayee(long payeeId) {
		return this.jdbcTemplate.queryForLong(
				"select count(*) from transaction where payeeid = ?", 
				new Object[]{payeeId});
	}
	
	@SuppressWarnings("deprecation")
	public long getNumTransactionsForCategory(long categoryId) {
		return this.jdbcTemplate.queryForLong(
				"select count(*) from transaction where categoryid = ?", 
				new Object[]{categoryId});
	}
	
	/*
	 * TODO: why are transactions not sorted by reverse date here?
	 */
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
	
	public List<FlatTransaction> getTransactionsForCategory(long categoryId, int limit) {
		return this.jdbcTemplate.query(
				FLAT_SELECT_CATEGORY +
						(limit > 0 ? String.format(" limit %d", limit) : ""), 
				new Object[]{categoryId, categoryId},
				new RowMapperUtil.FlatTransactionMapper());
	}
	
	public List<FlatTransaction> getTransactionsForPayee(long payeeId, int limit) {
		return this.jdbcTemplate.query(
				FLAT_SELECT + "where t.payeeid = ? order by t.entered desc " +
						(limit > 0 ? String.format("limit %d", limit) : ""), 
				new Object[]{payeeId},
				new RowMapperUtil.FlatTransactionMapper());
	}
	
	public SolrResponse<FlatTransaction> getTransactionsForPayee(long id) {
		return this.solrService.query(new SolrParams(new SolrConfig()).setPayeeId(id));
	}
	
	public SolrResponse<FlatTransaction> getTransactionsForCategory(long id) {
		return this.solrService.query(new SolrParams(new SolrConfig()).setCategoryId(id));
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

	public int delete(long id) {
		int num = this.jdbcTemplate.update("delete from transaction where id = ?", id);
		this.solrService.removeTransactionsById(id);
		return num;
	}	
}
