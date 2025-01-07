package com.slepeweb.money.service;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.User;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("userService")
public class UserServiceImpl extends BaseServiceImpl implements UserService {
	
	private static Logger LOG = Logger.getLogger(UserServiceImpl.class);

	public User save(User u) throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		if (u.isDefined4Insert()) {
			if (u.isInDatabase()) {
				User dbRecord = getUser(u.getAlias());		
				if (dbRecord != null) {
					return update(dbRecord, u);
				}
				else {
					throw new DataInconsistencyException(error(LOG, "Account does not exist in DB", u));
				}
			}
			else {
				return insert(u);
			}
		}
		else {
			throw new MissingDataException(error(LOG, "Account not saved - insufficient data", u));
		}
	}
	
	private User insert(User u) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into user (name, alias, password, enabled, roles) values (?, ?, ?, ?, ?)", 
					u.getName(), u.getAlias(), u.getPassword(), u.isEnabled(), u.getRoles());
			
			u.setId(getLastInsertId());	
			
			LOG.info(compose("Added new user", u));		
			return u;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("User already inserted");
		}
	}

	private User update(User dbRecord, User u) {
		if (! dbRecord.equals(u)) {
			dbRecord.assimilate(u);
			
			this.jdbcTemplate.update(
					"update account set name = ?, alias = ?, password = ?, enabled = ?, roles = ?", 
					dbRecord.getName(), dbRecord.getAlias(), dbRecord.getPassword(), dbRecord.isEnabled(), dbRecord.getRoles());
			
			LOG.info(compose("Updated user", u));
		}
		else {
			LOG.debug(compose("User not modified", u));
		}
		
		return dbRecord;
	}

	public User getUser(String alias) {
		try {
			return this.jdbcTemplate.queryForObject(
					"select * from user where alias = ?", 
					new RowMapperUtil.UserMapper(),
					alias);
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
