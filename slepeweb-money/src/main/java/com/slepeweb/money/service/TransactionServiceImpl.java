package com.slepeweb.money.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.Util;
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
					"a.id as accountid, a.origid as accountorigid, a.name as accountname, a.sortcode, a.accountno, a.rollno, " +
					"a.type as accounttype, a.openingbalance, a.balance as accountbalance, a.closed, a.note, a.reconciled as accountreconciled, " + 
					"p.id as payeeid, p.origid as payeeorigid, p.name as payeename, " + 
					"c.id as categoryid, c.origid as categoryorigid, c.major, c.minor, c.expense, " + 
					"t.id, t.origid, t.entered, t.memo, t.reference, t.amount, t.reconciled, " +
					"t.transferid, t.split " + FROM;
	
	public Transaction save(Transaction pt) 
			throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		
		return save(pt, false);
	}
		
	private Transaction save(Transaction formInput, boolean ignoreMirror) 
			throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		
		Account formInputMirrorAccount = null;
		Transfer formInputAsTransfer = null;
		Transaction transactionBeingUpdated = null;
		
		if (formInput.getId() > 0) {
			transactionBeingUpdated = get(formInput.getId());
		}
		
		if (formInput.isTransfer()) {
			formInputAsTransfer = (Transfer) formInput;
			formInputMirrorAccount = formInputAsTransfer.getMirrorAccount();
		}
		
		/*
		 *  If a Transaction is in fact a Transfer, then this is modelled as 2 transaction records ...
		 *  one is a 'mirror' of the other.
		 */
		Transaction mirroredTransaction = null;
		
		if (formInput.isDefined4Insert()) {
			Transaction savedTransaction;
			List<SplitTransaction> revisedSplits = new ArrayList<SplitTransaction>(formInput.getSplits());
			
			if (formInput.isInDatabase()) {
				// Update an existing transaction
				Transaction dbRecordToUpdate = get(formInput.getId());
				
				// We also need a preserved copy of the original transaction
				Transaction dbRecordBeforeUpdated = get(formInput.getId());
				
				if (dbRecordToUpdate != null) {
					// Beware - this next step assimilates pt into dbRecord, so dbRecord 
					// no longer contains the original transaction data
					savedTransaction = update(dbRecordToUpdate, formInput);
					
					// Now, adjust balances according to the changes to dbRecord
					adjustBalancesForUpdatedTransactions(dbRecordBeforeUpdated, dbRecordToUpdate);
				}
				else {
					throw new DataInconsistencyException(error(LOG, "Transaction does not exist in DB", formInput));
				}
			}
			else {
				// Insert a new transaction
				savedTransaction = insert(formInput);
				
				/*
				 *  Update the account balance, allowing for the fact that formInput might relate to a ScheduledTransaction,
				 *  in which case savedTransaction.getAccount().getBalance() can't be trusted. This next line ensures that the 
				 *  correct account balance is retrieved fresh from the database.
				 */
				this.accountService.adjustBalance(savedTransaction.getAmount(), savedTransaction.getAccount().getId());
			}

			// Also save the new transaction's splits, if any
			if (! savedTransaction.isTransfer()) {
				savedTransaction.assimilateSplits(revisedSplits);
				savedTransaction = this.splitTransactionService.save(savedTransaction);
			}
			
			// Keep a reference to the previous revision of this transaction, for
			// when we update mirror transactions
			savedTransaction.setPrevious(transactionBeingUpdated);
			
			// Manage mirror transaction, as applicable, including Solr updates for same
			if (! ignoreMirror) {
				// This might return null, indicating that an existing mirror transaction has been deleted
				mirroredTransaction = identifyMirrorTransactionIfRequired(savedTransaction, formInputMirrorAccount);
				
				if (mirroredTransaction != null) {
					// Recursive call to save(), but for the mirror transaction
					mirroredTransaction = save(mirroredTransaction, true /* ie. don't get stuck in an a recursive loop */);
					
					// Bind two transactions together.
					// Note that mirror is already bound to t, by manageMirror().
					savedTransaction.setTransferId(mirroredTransaction.getId());
					updateTransfer(savedTransaction.getId(), mirroredTransaction.getId());
				}
			}
			
			// Update solr regarding the transaction AND its splits, if any
			this.solrService4Money.save(savedTransaction);
			
			return savedTransaction;
		}
		else {
			String t = "Transaction not saved - insufficient data";
			LOG.error(compose(t, formInput));
			throw new MissingDataException(t);
		}
	}
	
	/*
	 * We are now tracking account balances each time a transaction is inserted or updated.
	 * When a transaction is updated, this might involve not only a change in the amount,
	 * but also an account change, so account updates for transfers need to be handled separately.
	 */
	private void adjustBalancesForUpdatedTransactions(Transaction original, Transaction updated) {
		long credit = 0;
		
		if (original.getAccount().getId() == updated.getAccount().getId()) {
			/*
			 *  No change observed regarding the account. 
			 *  credit is positive if new amount > original amount
			 */
			credit = updated.getAmount() - original.getAmount();
			
			if (Math.abs(credit) > 0) {
				this.accountService.adjustBalance(credit, updated.getAccount().getId());
			}
		}
		else {
			// The updated transaction relates to a different account!
			// Adjust the original account's balance
			this.accountService.adjustBalance(- original.getAmount(), original.getAccount().getId());

			// Now adjust the update account's balance
			this.accountService.adjustBalance(updated.getAmount(), updated.getAccount().getId());
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
	private Transaction identifyMirrorTransactionIfRequired(Transaction savedTransaction, Account formInputMirrorAccount) 
			throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		
		Transaction mirrorTransaction = null;
		boolean formInputSpecifiesTransfer = formInputMirrorAccount != null;
		boolean previousVersionWasTransfer = savedTransaction.getPrevious() != null && savedTransaction.getPrevious().getTransferId() > 0;
		long previousTransferId = previousVersionWasTransfer ? savedTransaction.getPrevious().getTransferId() : 0L;
		
		if (previousVersionWasTransfer && ! formInputSpecifiesTransfer) {
			// Case 3) delete the original mirror transaction
			LOG.info(String.format("Deleted %d transaction(s)", delete(previousTransferId, true)));
			this.solrService4Money.removeTransactionsById(previousTransferId);
		}
		else if (! previousVersionWasTransfer && formInputSpecifiesTransfer) {
			// Case 1) create a new mirror transaction
			mirrorTransaction = mirrorTransaction(savedTransaction, new Transfer().setSource(savedTransaction.getSource()), formInputMirrorAccount);
		}
		else if (previousVersionWasTransfer && formInputSpecifiesTransfer) {
			// Case 2) update an existing mirror
			Transaction origMirrorTransaction = get(previousTransferId);
			mirrorTransaction = mirrorTransaction(savedTransaction, origMirrorTransaction, formInputMirrorAccount);
		}
		
		return mirrorTransaction;
	}
	
	/*
	 * Populate and save a mirror transaction, and link it to the master transaction.
	 * Solr is NOT updated here.
	 */
	private Transaction mirrorTransaction(Transaction savedTransaction, Transaction base, Account formInputMirrorAccount)
			 throws MissingDataException, DuplicateItemException, DataInconsistencyException { 
		
		// Create new mirror transaction
		Transaction mirror = base;
		mirror.assimilate(base);
		mirror.setId(base.getId());
		
		if (! savedTransaction.isReconciled()) {
			mirror.
				setTransferId(savedTransaction.getId()).
				setAccount(formInputMirrorAccount).
				setPayee(savedTransaction.getPayee()).
				setCategory(savedTransaction.getCategory()).
				setEntered(savedTransaction.getEntered()).
				setMemo(savedTransaction.getMemo()).
				setAmount(- savedTransaction.getAmount());
		}
		else {
			// Only certain properties an be updated on reconciled transactions
			
			mirror.
				setPayee(savedTransaction.getPayee()).
				setCategory(savedTransaction.getCategory()).
				setEntered(savedTransaction.getEntered()).
				setMemo(savedTransaction.getMemo());
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
					t.isSplit(), t.getSource(), t.getOrigId(), Util.toSqlDate(t.getEntered()), t.getAmount(),
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
							Util.toSqlDate(dbRecord.getEntered()), 
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
	public LocalDate getTransactionDateForAccount(long accountId, boolean first) {
		String sql = String.format("select entered from transaction where accountid = ? order by entered %s limit 1", 
				first ? "" : "desc");
		
		List<LocalDate> list = this.jdbcTemplate.query(
				sql, 
				new RowMapperUtil.TransactionDateMapper(),
				new Object[]{accountId});
		
		if (list.size() == 1) {
			return list.get(0);
		}
		
		return first ? Util.dayZero() : Util.today();
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
	
	public List<Transaction> getTransactionsByDate(LocalDate from, LocalDate to) {
		return getTransactions(
				SELECT + "where t.entered >= ? and t.entered <= ? order by t.entered", 
				Util.toSqlDate(from), Util.toSqlDate(to));
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
	
	public List<Transaction> getTransactionsForAccount(long accountId, LocalDate from, LocalDate to) {
		return getTransactions(
				SELECT + "where t.accountid = ? and t.entered >= ? and t.entered <= ? order by t.entered", 
				accountId, Util.toSqlDate(from), Util.toSqlDate(to));
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
	
	public long calculateBalance(long accountId) {
		return calculateBalance(accountId, null);
	}
	
	public long calculateBalance(long accountId, LocalDate to) {
		StringBuilder sb = new StringBuilder("select sum(amount) from transaction where accountid = ? ");
		int arrlen = 1 + (to != null ? 1 : 0);
		Object[] params = new Object[arrlen];
		int index = 0;
		params[index++] = accountId;
		
		if (to != null) {
			sb.append("and entered <= ? ");
			params[index++] = Util.toSqlDate(to);
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
		LOG.info(compose("Deleted transaction by id", id));
		
		// Update account balance
		this.accountService.adjustBalance(- t.getAmount(), t.getAccount().getId());
		
		return num;
	}
}
