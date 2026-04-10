package com.slepeweb.money.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.SavingsAccount;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("accountService")
public class AccountServiceImpl extends BaseServiceImpl implements AccountService {
	
	private static Logger LOG = Logger.getLogger(AccountServiceImpl.class);

	@Autowired private TransactionService transactionService;
	@Autowired private SolrService4Money solrService4Money;
	
	public Account save(Account a) throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		if (a.isDefined4Insert()) {
			if (a.isInDatabase()) {
				// Get the record in the db so that we can detect any name change
				Account dbRecord = get(a.getId());
				
				if (dbRecord != null) {
					return update(dbRecord, a);
				}
				else {
					throw new DataInconsistencyException(error(LOG, "Account does not exist in DB", a));
				}
			}
			else {
				return insert(a);
			}
		}
		else {
			throw new MissingDataException(error(LOG, "Account not saved - insufficient data", a));
		}
	}
	
	private Account insert(Account a) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into account (origid, name, type, sortcode, accountno, rollno, openingbalance, closed, " + 
							"note, reconciled, balance) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
					a.getOrigId(), a.getName(), a.getType(), a.getSortCode(), a.getAccountNo(), a.getRollNo(),
					a.getOpeningBalance(), a.isClosed(), a.getNote(), a.getReconciled(), a.getBalance());
			
			a.setId(getLastInsertId());	
			
			if (a instanceof SavingsAccount) {
				SavingsAccount sa = (SavingsAccount) a;
				insertNewSavings(sa);
			}
	
			LOG.info(compose("Added new account", a));		
			return a;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Account already inserted");
		}
	}
	
	private void insertNewSavings(SavingsAccount sa) {
		this.jdbcTemplate.update(
				"insert into savings (accountid, matures, access, schedule, owner, rate) values (?, ?, ?, ?, ?, ?)", 
				sa.getId(), sa.getMatures(), sa.getAccess(), sa.getSchedule(), sa.getOwner(), sa.getRate());
	}

	public Account update(Account dbRecord, Account a) {
		if (! dbRecord.equals(a)) {
			boolean nameChanged = ! dbRecord.getName().equals(a.getName());
			
			// Eliminate redundant data according to account type
			if (! (a.isCurrent() || a.isSavings())) {
				a.setSortCode(null).setAccountNo(null).setRollNo(null);
			}
			
			/*
			 *  NO NEED to call Account.assimilate(), because Account 'a' is e fully populated by accountForm.jsp.
			 *  Attempts to retain the usual call to assimilate() ran into difficulties when the new SavingsAccount
			 *  class was introduced. Enough said!
			 */
			
			// Do NOT update balance here - that's the job of saveBalance()
			this.jdbcTemplate.update(
					"update account set name = ?, type = ?, sortcode = ?, accountno = ?, rollno = ?, openingbalance = ?, " + 
							"closed = ?, note = ?, reconciled = ? where id = ?", 
					a.getName(), a.getType(), a.getSortCode(), a.getAccountNo(),
					a.getRollNo(), a.getOpeningBalance(), a.isClosed(), 
					a.getNote(), a.getReconciled(), a.getId());
			
			if (nameChanged) {
				// Update transaction documents in solr, which store account name, NOT id.
				this.solrService4Money.save(this.transactionService.getTransactionsForAccount(a.getId()));
			}
			
			/*
			 * There are 3 scenarios to consider regarding updates and savings accounts:
			 * a) account type has changed TO savings
			 * b) account type WAS savings
			 * c) account type REMAINs savings
			 */
			if (dbRecord.isSavings() && a.isSavings()) {
				
				// Case c)
				SavingsAccount saRecord = (SavingsAccount) dbRecord;
				SavingsAccount sa = (SavingsAccount) a;
				
				/* Cater for situation where a savings account does not have a corresponding entry in the 
				 * savings table. In this case, dbRecord (being a SavingsAccount object) will have a
				 * null value in the 'accountId field', and so a new record needs to be added rather than
				 * an existing record being updated.
				 */
				if (saRecord.isLinked()) {
					this.jdbcTemplate.update(
							"update savings set matures = ?, access = ?, schedule = ?, owner = ?, rate = ? where accountid = ?", 
							sa.getMatures(), sa.getAccess(), sa.getSchedule(), sa.getOwner(), 
							sa.getRate(), sa.getId());
				}
				else {
					insertNewSavings(sa);
				}
			}
			else if (dbRecord.isSavings() && ! a.isSavings()) {
				
				// Case b)
				this.jdbcTemplate.update("delete from savings where accountid = ?", a.getId());
			}
			else  if (! dbRecord.isSavings() && a.isSavings()){
				
				// Case a)
				insertNewSavings((SavingsAccount) a);
			}
			
			LOG.info(compose("Updated account", a));
		}
		else {
			LOG.debug(compose("Account not modified", a));
		}
		
		return dbRecord;
	}
	
	public void updateReconciled(Account a) {
		this.jdbcTemplate.update("update account set reconciled = ? where id = ?", a.getReconciled(), a.getId());
	}

	public Account get(String name) {
		return get(buildSelect("name = ?"), name);
	}

	public Account get(long id) {
		return get(buildSelect("id = ?"), id);
	}
	
	public Account getByOrigId(long origId) {
		return get(buildSelect("origid = ?"), origId);
	}
	
	private Account get(String sql, Object... params) {
		try {
			return this.jdbcTemplate.queryForObject(
				sql, new RowMapperUtil.AccountMapper(), params);
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Account> getAll() {
		return getAll(false);
	}
	
	private String buildSelect(String whereClause) {
		return "select * from account a left join savings sa on sa.accountid = a.id " +
			(StringUtils.isNotBlank(whereClause) ? "where " + whereClause : "");
	}

	public List<Account> getAll(boolean includingClosed) {
		String sql = buildSelect(includingClosed ? "" : "closed=false") + " order by a.name";
		return this.jdbcTemplate.query(sql, new RowMapperUtil.AccountMapper());
	}
	
	public List<Account> getAllSavings() {
		String sql = buildSelect("closed=false and type='savings'") + " order by matures";
		return this.jdbcTemplate.query(sql, new RowMapperUtil.AccountMapper());
	}
	
	public void resetBalances() throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		long balanceWas, balanceNow;
		int count = 0;
		
		for (Account a : getAll(false) ) {
			balanceWas = a.getBalance();
			balanceNow = this.transactionService.calculateBalance(a.getId());
			
			if (balanceNow != balanceWas) {
				a.setBalance(balanceNow);
				saveBalance(a);
				count += 1;
				
				LOG.warn(String.format("Balance for account %s was %s, and has been reset to %s", 
						a.getName(), Util.formatPounds(balanceWas), Util.formatPounds(balanceNow)));
			}
		}
		
		LOG.info(String.format("%s account balances were reset", count));
	}
	
	public List<Account> getAssets() {
		String sql = buildSelect("type != 'other'") + " order by a.name";
		return this.jdbcTemplate.query(sql, new RowMapperUtil.AccountMapper());
	}
	
	/*
	 * It's only possible to delete an account if there are ZERO transactions for the account.
	 */
	public int delete(long id) {
		if (this.transactionService.getNumTransactionsForAccount(id) > 0) {
			return 0;
		}

		return this.jdbcTemplate.update("delete from account where id = ?", id);
	}
	
	public void saveBalance(Account a) {
		this.jdbcTemplate.update("update account set balance = ? where id = ?", a.getBalance(), a.getId()); 
	}
}
