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
import com.slepeweb.money.bean.Transfer;
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
			
			// Also save this transaction's splits, if any
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
			this.solrService.save(t);
			
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
			this.solrService.removeTransactionsById(previousTransferId);
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
		Transaction mirror = base.
				setXferId(t.getId()).
				setAccount(transferAccount).
				setPayee(t.getPayee()).
				setCategory(t.getCategory()).
				setEntered(t.getEntered()).
				setMemo(t.getMemo()).
				setAmount(- t.getAmount());
		
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
		if (! dbRecord.equals(t)) {
			dbRecord.assimilate(t);
			
			try {
				this.jdbcTemplate.update(
						"update transaction set entered = ?, " + 
						"accountid = ?, payeeid = ?, categoryid = ?, " + 
						"split = ?, amount = ?, reconciled = ?, " +
						"memo = ?, reference = ?, transferid = ? " +
						"where id = ?", 
						dbRecord.getEntered(), 
						dbRecord.getAccount().getId(), dbRecord.getPayee().getId(), dbRecord.getCategory().getId(), 
						dbRecord.isSplit(), dbRecord.getAmount(), dbRecord.isReconciled(), 
						dbRecord.getMemo(), dbRecord.getReference(), dbRecord.getTransferId(),
						dbRecord.getId());
				
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

	public Transaction get(long id) {
		return get(SELECT + "where t.id = ?", new Object[]{id});
	}

	public Transaction getByOrigId(int source, long origId) {
		return get(SELECT + "where t.source = ? and t.origid = ?", new Object[]{source, origId});
	}
	
	private Transaction get(String sql, Object[] params) {
		Transaction t = (Transaction) getFirstInList(this.jdbcTemplate.query(
				sql, params, new RowMapperUtil.TransactionMapper()));
		
		if (t != null) {
			if (t.isSplit()) {
				t.setSplitsService(this.splitTransactionService);
			}
			
			if (t.isTransfer()) {
				return new Transfer(t).setTransactionService(this);
			}
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
		return getTransactions(
				SELECT + "where t.accountid = ? order by t.entered", 
				new Object[]{accountId});
	}
	
	public List<Transaction> getTransactionsByDate(Date from, Date to) {
		return getTransactions(
				SELECT + "where t.entered >= ? and t.entered <= ? order by t.entered", 
				new Object[]{from, to});
	}
	
	public List<Transaction> getAll() {
		return getTransactions(SELECT + "order by t.entered", new Object[]{});
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
		return getTransactions(
				SELECT + "where t.accountid = ? and t.entered >= ? and t.entered <= ? order by t.entered", 
				new Object[]{accountId, from, to});
	}
	
	private List<Transaction> getTransactions(String sql, Object[] params) {
		List<Transaction> list = this.jdbcTemplate.query(
				sql, params, new RowMapperUtil.TransactionMapper());
		
		for (Transaction t : list) {
			if (t.isSplit()) {
				t.setSplits(this.splitTransactionService.get(t));
			}
		}
		
		return list;
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
		return delete(id, false);
	}
	
	private int delete(long id, boolean ignoreMirror) {
		// First check whether this is a transfer; if so, delete the parallel transaction too
		int num = 0;
		Transaction t = get(id);
		
		if (! ignoreMirror && t.isTransfer()) {
			num += delete(t.getTransferId(), true);
			this.solrService.removeTransactionsById(t.getTransferId());
		}
		
		num += this.jdbcTemplate.update("delete from transaction where id = ?", id);		
		this.solrService.removeTransactionsById(id);
		return num;
	}	
}
