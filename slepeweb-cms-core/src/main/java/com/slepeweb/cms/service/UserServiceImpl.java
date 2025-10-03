package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class UserServiceImpl extends BaseServiceImpl implements UserService {
	
	private static Logger LOG = Logger.getLogger(UserServiceImpl.class);
	
	private final static String SELECT_TEMPLATE = 
			"select * " +
			"from user " +
			"where %s";
	
	public User save(User u) {
		return save(u, null, false);
	}
	
	public User save(User u, Site s, boolean doRoles) {
		if (u.isDefined4Insert()) {
			User dbRecord = get(u.getId());		
			if (dbRecord != null) {
				update(dbRecord, u);
				return dbRecord;
			}
			else {
				insert(u);
			}
			
			if (doRoles) {
				saveRoles(u, s);
			}
		}
		else {
			LOG.error(compose("User not saved - insufficient data", u));
		}
		
		return u;
	}
	
	public void saveRoles(User u, Site s) {
		deleteUserRoles(u, s);
		for (String role : u.getRoles(s.getId())) {
			insertUserRole(u, s, role);
		}
	}
	
	private void insertUserRole(User u, Site s, String r) {
		this.jdbcTemplate.update( "insert into role (userid, siteid, role) values (?,?,?)", 
				u.getId(), s.getId(), r);	
	}
	
	private void deleteUserRoles(User u, Site s) {
		if (this.jdbcTemplate.update("delete from role where userid = ? and siteid = ?", u.getId(), s.getId()) > 0) {
			LOG.warn(compose("Deleted user roles", u.getId(), s.getId()));
		}
	}
	
	private User insert(User u) {		
		this.jdbcTemplate.update( 
				"insert into user (alias, firstname, lastname, email, phone, password, editor, enabled, qanda, secret) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
				u.getAlias(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getPhone(), u.getPassword(), u.isEditor(), 
				u.isEnabled(), u.getQandA(), u.getSecret());	
		
		u.setId(getLastInsertId());			
		LOG.info(compose("Added new user", u));		
		return u;
	}

	private void update(User dbRecord, User u) {
		if (! dbRecord.equals(u)) {
			dbRecord.assimilate(u);
			
			this.jdbcTemplate.update(
					"update user set alias=?, firstname=?, lastname=?, email=?, phone=?, password=?, editor=?, enabled=?, qanda=?, secret=? where id=?", 
					dbRecord.getAlias(), dbRecord.getFirstName(), dbRecord.getLastName(), dbRecord.getEmail(), dbRecord.getPhone(),  
					dbRecord.getPassword(), dbRecord.isEditor(), dbRecord.isEnabled(), dbRecord.getQandA(), dbRecord.getSecret(), dbRecord.getId());
			
			LOG.info(compose("Updated user", u));
		}
		else {
			u.setId(dbRecord.getId());
			LOG.info(compose("User not modified", u));
		}
	}

	public User partialUpdate(User u) {
		this.jdbcTemplate.update(
				"update user set password=?, enabled=?, secret=? where id=?", 
				u.getPassword(), u.isEnabled(), u.getSecret(), u.getId());
		
		return u;
	}
	
	public void delete(Long id) {
		if (this.jdbcTemplate.update("delete from user where id = ?", id) > 0) {
			LOG.warn(compose("Deleted user", id));
		}
	}

	public User get(String alias) {
		return get(String.format(SELECT_TEMPLATE, " alias = ?"), alias);
	}

	public User get(Long id) {
		return get(String.format(SELECT_TEMPLATE, " id = ?"), id);
	}
	
	public List<User> getAll() {
		return this.jdbcTemplate.query(
				"select * from user", new RowMapperUtil.UserMapper());
	}
	
	public User getBySecret(String secret) {
		return get(String.format(SELECT_TEMPLATE, " secret = ?"), secret);
	}

	public User getByPassword(String pwd) {
		return get(String.format(SELECT_TEMPLATE, " password = ?"), pwd);
	}

	public User getByEmail(String email) {
		return get(String.format(SELECT_TEMPLATE, " email = ?"), email);
	}

	private User get(String sql, Object... params) {
		return (User) getFirstInList(this.jdbcTemplate.query(
			sql, new RowMapperUtil.UserMapper(), params));
	}

	public List<String> getRoles(Long userId, Long siteId) {
		return this.jdbcTemplate.query(
				"select role from role where userid = ? and siteid = ? ", 
				new RowMapperUtil.RoleMapper(), userId, siteId);
	}	
}
