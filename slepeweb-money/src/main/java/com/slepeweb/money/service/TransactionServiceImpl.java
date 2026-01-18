package com.slepeweb.money.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.Transfer;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("transactionService")
public class TransactionServiceImpl extends BaseServiceImpl implements TransactionService {
	
	private static Logger LOG = Logger.getLogger(TransactionServiceImpl.class);
	@Autowired private AccountService accountService;
	@Autowired private SplitTransactionService splitTransactionService;
	@Autowired private SolrService4Money solrService4Money;
	
	private static final String FROM = 
			"from transaction t " +
			"join account a on a.id = t.accountid " + 
			"join payee p on p.id = t.payeeid " +
			"join category c on c.id = t.categoryid ";
	
	private static final String SELECT = 
			"select " +
					"a.id as accountid, a.origid as accountorigid, a.name as accountname, " +
					"a.type as accounttype, a.openingbalance, a.closed, a.note, a.reconciled as accountreconciled, " + 
					"p.id as payeeid, p.origid as payeeorigid, p.name as payeename, " + 
					"c.id as categoryid, c.origid as categoryorigid, c.major, c.minor, c.expense, " + 
					"t.id, t.origid, t.entered, t.memo, t.reference, t.amount, t.reconciled, " +
					"t.transferid, t.split " + FROM;
	
	public Transaction save(Transaction pt) 
			throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		
		return save(pt, false);
	}
		
	private Transaction save(Transaction pt, boolean ignoreMirror) 
			throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		
		Account mirrorAccount = null;
		boolean isTransfer = pt instanceof Transfer;
		Transfer tt = null;
		Transaction previous = null;
		
		if (pt.getId() > 0) {
			previous = get(pt.getId());
		}
		
		if (isTransfer) {
			tt = (Transfer) pt;
			mirrorAccount = tt.getMirrorAccount();
		}
		
		Transaction mirror = null;
		
		if (pt.isDefined4Insert()) {
			Transaction t;
			List<SplitTransaction> revisedSplits = new ArrayList<SplitTransaction>(pt.getSplits());
			
			if (pt.isInDatabase()) {
				Transaction dbRecord = get(pt.getId());	
				if (dbRecord != null) {
					t = update(dbRecord, pt);
				}
				else {
					throw new DataInconsistencyException(error(LOG, "Transaction does not exist in DB", pt));
				}
			}
			else {
				t = insert(pt);
			}
			
			// Also save the new transaction's splits, if any
			t.assimilateSplits(revisedSplits);
			t = this.splitTransactionService.save(t);
			
			// Keep a reference to the previous revision of this transaction, for
			// when we update mirror transactions
			t.setPrevious(previous);
			
			// Manage mirror transaction, as applicable, including Solr updates for same
			if (! ignoreMirror) {
				mirror = manageMirror(t, mirrorAccount);
				
				if (mirror != null) {
					// Recursive call to save(), but for the mirror transaction
					mirror = save(mirror, true);
					
					// Bind two transactions together.
					// Note that mirror is already bound to t, by manageMirror().
					t.setXferId(mirror.getId());
					updateTransfer(t.getId(), mirror.getId());
				}
			}
			
			// Update solr regarding the transaction AND its splits, if any
			this.solrService4Money.save(t);
			
			return t;
		}
		else {
			String t = "Transaction not saved - insufficient data";
			LOG.error(compose(t, pt));
			throw new MissingDataException(t);
		}
	}
	
	/*
	 * This method has to do one of 3 things:
	 * 	1) Create a new mirror transaction, and bind to the master
	 * 	2) Update an existing mirror transaction
	 * 	3) Delete an existing mirror transaction, should the master change back to a normal payment
	 * 
	 * Must only be called for Transfer objects, and NOT Transaction objects.
	 */
	private Transaction manageMirror(Transaction t, Account mirrorAccount) 
			throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		
		Transaction mirror = null;
		long previousTransferId = 0L;
		
		if (t.getPrevious() != null) {
			previousTransferId = t.getPrevious().getTransferId();
		}		
		
		if (previousTransferId > 0 && mirrorAccount == null) {
			// Case 3) delete the original mirror transaction
			LOG.info(String.format("Deleted %d transaction(s)", delete(previousTransferId, true)));
			this.solrService4Money.removeTransactionsById(previousTransferId);
		}
		else if (previousTransferId == 0 && mirrorAccount != null) {
			// Case 1) create a new mirror transaction
			mirror = mirrorTransaction(t, new Transaction().setSource(-1), mirrorAccount);
		}
		else if (previousTransferId > 0 && mirrorAccount != null) {
			// Case 2) update an existing mirror
			Transaction origMirrorTransaction = get(previousTransferId);
			if (origMirrorTransaction != null) {
				mirror = mirrorTransaction(t, origMirrorTransaction, mirrorAccount);
			}
		}
		
		return mirror;
	}
	
	/*
	 * Populate and save a mirror transaction, and link it to the master transaction.
	 * Solr is NOT updated here.
	 */
	private Transaction mirrorTransaction(Transaction t, Transaction base, Account transferAccount)
			 throws MissingDataException, DuplicateItemException, DataInconsistencyException { 
		
		// Create new mirror transaction
		Transaction mirror = base;
		
		if (! t.isReconciled()) {
			mirror.
				setXferId(t.getId()).
				setAccount(transferAccount).
				setPayee(t.getPayee()).
				setCategory(t.getCategory()).
				setEntered(t.getEntered()).
				setMemo(t.getMemo()).
				setAmount(- t.getAmount());
		}
		else {
			// Only certain properties an be updated on reconciled transactions
			
			mirror.
				setPayee(t.getPayee()).
				setCategory(t.getCategory()).
				setEntered(t.getEntered()).
				setMemo(t.getMemo());
		}
		
		return mirror;
	}
	
	private Transaction insert(Transaction t) throws MissingDataException, DuplicateItemException {
		
		try {
			/* 
			 * The source column is set on first entry, and never updated.
			 * It is only relevant to the import of MSMoney data.
			 */
			this.jdbcTemplate.update(
					"insert into transaction (accountid, payeeid, categoryid, split, source, origid, entered, amount, " +
					"reconciled, transferid, reference, memo) " +
					"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
					t.getAccount().getId(), t.getPayee().getId(), t.getCategory().getId(), 
					t.isSplit(), t.getSource(), t.getOrigId(), t.getEntered(), t.getAmount(),
					false, t.getTransferId(), t.getReference(), t.getMemo());
			
			t.setId(getLastInsertId());	
			LOG.info(compose("Added new transaction", t));		
			return t;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Transaction already inserted");
		}
	}

	public Transaction update(Transaction dbRecord, Transaction t) {
		if (! dbRecord.equals(t)) {
			dbRecord.assimilate(t);
			
			try {
				if (! t.isReconciled()) {
					this.jdbcTemplate.update(
							"update transaction set entered = ?, " + 
							"accountid = ?, payeeid = ?, categoryid = ?, " + 
							"split = ?, amount = ?, " +
							"memo = ?, reference = ?, transferid = ? " +
							"where id = ?", 
							dbRecord.getEntered(), 
							dbRecord.getAccount().getId(), dbRecord.getPayee().getId(), dbRecord.getCategory().getId(), 
							dbRecord.isSplit(), dbRecord.getAmount(), 
							dbRecord.getMemo(), dbRecord.getReference(), dbRecord.getTransferId(),
							dbRecord.getId());
				}
				else {
					this.jdbcTemplate.update(
							"update transaction set payeeid = ?, categoryid = ?, memo = ? " +
							"where id = ?", 
							dbRecord.getPayee().getId(), dbRecord.getCategory().getId(), 
							dbRecord.getMemo(), dbRecord.getId());
				}
				
				LOG.info(compose("Updated transaction", dbRecord));
			}
			catch (DuplicateKeyException e) {
				LOG.error(compose("Duplicate key", dbRecord));
			}
		}
		else {
			LOG.debug(compose("Transaction not modified", dbRecord));
		}
		
		return dbRecord;
	}

	public void updateSplit(Transaction t) {
		this.jdbcTemplate.update(
				"update transaction set split = ? where id = ?", 
				t.isSplit(), t.getId());
		
		LOG.info(compose("Updated split flag for transaction", t));
	}

	public void updateTransfer(Long id, Long mirrorId) {
		this.jdbcTemplate.update(
				"update transaction set transferid = ? where id = ?", 
				mirrorId, id);
		
		this.jdbcTemplate.update(
				"update transaction set transferid = ? where id = ?", 
				id, mirrorId);
		
		LOG.info(compose("Updated transfer details", id, mirrorId));
	}

	public void updateReconciled(long id) {
		this.jdbcTemplate.update("update transaction set reconciled = 1 where id = ?", id);
	}

	public Transaction get(long id) {
		return get(SELECT + "where t.id = ?", id);
	}

	public Transaction getByOrigId(int source, long origId) {
		return get(SELECT + "where t.source = ? and t.origid = ?", source, origId);
	}
	
	private Transaction get(String sql, Object... params) {
		Transaction t = null;
		
		try {
			t = this.jdbcTemplate.queryForObject(
				sql, new RowMapperUtil.TransactionMapper(), params);
		}
		catch (EmptyResultDataAccessException e) {}
		
		if (t != null) {
			if (t.isSplit()) {
				t.setSplits(this.splitTransactionService);
			}
			
			if (t.isTransfer()) {
				return ((Transfer) t).setTransactionService(this);
			}
		}
								
		return t;
	}

	// TODO: review call to this method
	public Timestamp getTransactionDateForAccount(long accountId, boolean first) {
		String sql = String.format("select entered from transaction where accountid = ? order by entered %s limit 1", 
				first ? "" : "desc");
		
		List<Timestamp> list = this.jdbcTemplate.query(
				sql, 
				new RowMapperUtil.TransactionDateMapper(),
				new Object[]{accountId});
		
		if (list.size() == 1) {
			return list.get(0);
		}
		
		return first ? new Timestamp(0L) : new Timestamp(new Date().getTime());
	}
	
	public List<Transaction> getTransactionsForAccount(long accountId) {
		return getTransactions(
				SELECT + "where t.accountid = ? order by t.entered", 
				accountId);
	}
	
	public List<Transaction> getUnreconciled(long accountId) {
		LOG.info("Getting un-reconciled transactions");
		return getTransactions(
				SELECT + "where t.accountid = ? and t.reconciled = 0 order by t.entered limit 500", 
				accountId);
	}
	
	public List<Transaction> getTransactionsForPayee(long payeeId) {
		return getTransactions(
				SELECT + "where t.payeeid = ? order by t.entered", 
				payeeId);
	}
	
	public Transaction getLastTransactionsForPayee(long payeeId) {
		return this.jdbcTemplate.queryForObject(
				SELECT + "where t.payeeid = ? order by t.entered desc limit 1", 
				new RowMapperUtil.TransactionMapper(),
				payeeId);
	}
	
	public List<Transaction> getTransactionsForCategory(long categoryId) {
		return getTransactions(
				SELECT + "where t.categoryid = ? order by t.entered", 
				categoryId);
	}
	
	public List<Transaction> getTransactionsByDate(Date from, Date to) {
		return getTransactions(
				SELECT + "where t.entered >= ? and t.entered <= ? order by t.entered", 
				from, to);
	}
	
	public List<Transaction> getAll() {
		return getTransactions(SELECT + "order by t.entered", 0L);
	}
	
	public long getNumTransactionsForAccount(long accountId) {
		return this.jdbcTemplate.queryForObject(
				"select count(*) from transaction where accountid = ?", 
				Long.class,
				accountId);
	}
	
	public long getNumTransactionsForPayee(long payeeId) {
		return this.jdbcTemplate.queryForObject(
				"select count(*) from transaction where payeeid = ?", 
				Long.class,
				payeeId);
	}
	
	public long getNumTransactionsForCategory(long categoryId) {
		return this.jdbcTemplate.queryForObject(
				"select count(*) from transaction where categoryid = ?", 
				Long.class,
				categoryId);
	}
	
	public List<Transaction> getTransactionsForAccount(long accountId, Timestamp from, Timestamp to) {
		return getTransactions(
				SELECT + "where t.accountid = ? and t.entered >= ? and t.entered <= ? order by t.entered", 
				accountId, from, to);
	}
	
	private List<Transaction> getTransactions(String sql, Object... params) {
		List<Transaction> list = this.jdbcTemplate.query(
				sql, new RowMapperUtil.TransactionMapper(), params);
		
		for (Transaction t : list) {
			if (t.isSplit()) {
				t.setSplits(this.splitTransactionService);
			}
			
			if (t.isTransfer()) {
				((Transfer) t).setTransactionService(this);
			}
		}
		
		return list;
	}
	
	public long getBalance(long accountId) {
		return getBalance(accountId, null);
	}
	
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
		
		Long sum = this.jdbcTemplate.queryForObject(sb.toString(), Long.class, params);
		Account a = this.accountService.get(accountId);
		return a.getOpeningBalance() + (sum == null ? 0 : sum.longValue());
	}

	public int delete(long id) {
		return delete(id, false);
	}
	
	private int delete(long id, boolean ignoreMirror) {
		// First check whether this is a transfer; if so, delete the parallel transaction too
		int num = 0;
		Transaction t = get(id);
		
		if (! ignoreMirror && t.isTransfer()) {
			num += delete(t.getTransferId(), true);
			this.solrService4Money.removeTransactionsById(t.getTransferId());
		}
		
		num += this.jdbcTemplate.update("delete from transaction where id = ?", id);		
		this.solrService4Money.removeTransactionsById(id);
		return num;
	}	
}
