package com.slepeweb.money.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Account;
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
					"insert into account (origid, name, type, openingbalance, closed, note, reconciled) values (?, ?, ?, ?, ?, ?, ?)", 
					a.getOrigId(), a.getName(), a.getType(), a.getOpeningBalance(), a.isClosed(), a.getNote(), a.getReconciled());
			
			a.setId(getLastInsertId());	
			
			LOG.info(compose("Added new account", a));		
			return a;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Account already inserted");
		}
	}

	public Account update(Account dbRecord, Account a) {
		if (! dbRecord.equals(a)) {
			boolean nameChanged = ! dbRecord.getName().equals(a.getName());
			
			dbRecord.assimilate(a);
			
			this.jdbcTemplate.update(
					"update account set name = ?, type = ?, openingbalance = ?, closed = ?, note = ?, reconciled = ? where id = ?", 
					dbRecord.getName(), dbRecord.getType(), dbRecord.getOpeningBalance(), dbRecord.isClosed(), 
					dbRecord.getNote(), dbRecord.getReconciled(), dbRecord.getId());
			
			if (nameChanged) {
				// Update transaction documents in solr, which store account name, NOT id.
				this.solrService4Money.save(this.transactionService.getTransactionsForAccount(dbRecord.getId()));
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
		return get("select * from account where name = ?", name);
	}

	public Account get(long id) {
		return get("select * from account where id = ?", id);
	}
	
	public Account getByOrigId(long id) {
		return get("select * from account where origid = ?", id);
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

	public List<Account> getAll(boolean includingClosed) {
		String sql = String.format("select * from account %s order by name", includingClosed ? "" : "where closed=false");
		return this.jdbcTemplate.query(sql, new RowMapperUtil.AccountMapper());
	}
	
	public List<Account> getAllWithBalances() {
		String sql = "select * from account where closed=false order by type, name";
		List<Account> accounts =  this.jdbcTemplate.query(sql, new RowMapperUtil.AccountMapper());
		
		for (Account a : accounts ) {
			a.setBalance(this.transactionService.getBalance(a.getId()));
		}
		
		return accounts;
	}
	
	public List<Account> getAssets() {
		//String sql = "select * from account where type in ('current', 'savings', 'pension') order by name";
		String sql = "select * from account where type != 'other' order by name";
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
}
