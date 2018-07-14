package com.slepeweb.money.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("accountService")
public class AccountServiceImpl extends BaseServiceImpl implements AccountService {
	
	private static Logger LOG = Logger.getLogger(AccountServiceImpl.class);
	
	public Account save(Account a) throws MissingDataException, DuplicateItemException {
		if (a.isDefined4Insert()) {
			Account dbRecord = get(a.getName());		
			if (dbRecord != null) {
				update(dbRecord, a);
				return dbRecord;
			}
			else {
				insert(a);
			}
		}
		else {
			String t = "Account not saved - insufficient data";
			LOG.error(compose(t, a));
			throw new MissingDataException(t);
		}
		
		return a;
	}
	
	private Account insert(Account a) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into account (name, openingbalance, closed, note) values (?, ?, ?, ?)", 
					a.getName(), a.getOpeningBalance(), a.isClosed(), a.getNote());
			
			a.setId(getLastInsertId());	
			
			LOG.info(compose("Added new account", a));		
			return a;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Account already inserted");
		}
	}

	private void update(Account dbRecord, Account a) {
		if (! dbRecord.equals(a)) {
			dbRecord.assimilate(a);
			
			this.jdbcTemplate.update(
					"update account set name = ?, openingbalance = ?, closed = ?, note = ? where id = ?", 
					dbRecord.getName(), dbRecord.getOpeningBalance(), dbRecord.isClosed(), dbRecord.getNote(), dbRecord.getId());
			
			LOG.info(compose("Updated account", a));
		}
		else {
			LOG.info(compose("Account not modified", a));
		}
	}

	public Account get(String name) {
		return get("select * from account where name = ?", new Object[]{name});
	}

	public Account get(long id) {
		return get("select * from account where id = ?", new Object[]{id});
	}
	
	private Account get(String sql, Object[] params) {
		return (Account) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.AccountMapper()));
	}

	public List<Account> getAll() {
		return this.jdbcTemplate.query(
			"select * from account order by name", new RowMapperUtil.AccountMapper());
	}
}
