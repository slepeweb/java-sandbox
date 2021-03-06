package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Role;
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
		return save(u, false);
	}
	
	public User save(User u, boolean doRoles) {
		if (u.isDefined4Insert()) {
			User dbRecord = get(u.getEmail());		
			if (dbRecord != null) {
				update(dbRecord, u);
				return dbRecord;
			}
			else {
				insert(u);
			}
			
			if (doRoles) {
				saveRoles(u);
			}
		}
		else {
			LOG.error(compose("User not saved - insufficient data", u));
		}
		
		return u;
	}
	
	public void saveRoles(User u) {
		deleteUserRoles(u);
		for (Role role : u.getRoles()) {
			insertUserRole(u, role);
		}
	}
	
	private void insertUserRole(User u, Role r) {
		this.jdbcTemplate.update( "insert into user_role (userid, roleid) values (?,?)", 
				u.getId(), r.getId());	
	}
	
	private void deleteUserRoles(User u) {
		if (this.jdbcTemplate.update("delete from user_role where userid = ?", u.getId()) > 0) {
			LOG.warn(compose("Deleted user roles", u.getId()));
		}
	}
	
	private User insert(User u) {		
		this.jdbcTemplate.update( "insert into user (firstname, lastname, email, phone, password, enabled, secret) values (?, ?, ?, ?, ?, ?, ?)", 
				u.getFirstName(), u.getLastName(), u.getEmail(), u.getPhone(), u.getPassword(), u.isEnabled(), u.getSecret());	
		
		u.setId(getLastInsertId());			
		LOG.info(compose("Added new user", u));		
		return u;
	}

	private void update(User dbRecord, User u) {
		if (! dbRecord.equals(u)) {
			dbRecord.assimilate(u);
			
			this.jdbcTemplate.update(
					"update user set firstname=?, lastname=?, phone=?, password=?, enabled=?, secret=? where id=?", 
					dbRecord.getFirstName(), dbRecord.getLastName(), dbRecord.getPhone(),  
					dbRecord.getPassword(), dbRecord.isEnabled(), dbRecord.getSecret(), dbRecord.getId());
			
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

	public User get(String email) {
		return get(String.format(SELECT_TEMPLATE, " email = ?"), new Object[]{email});
	}

	public User get(Long id) {
		return get(String.format(SELECT_TEMPLATE, " id = ?"), new Object[]{id});
	}
	
	public User getBySecret(String secret) {
		return get(String.format(SELECT_TEMPLATE, " secret = ?"), new Object[]{secret});
	}

	private User get(String sql, Object[] params) {
		return (User) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.UserMapper()));
	}

	public List<Role> getRoles(Long userId) {
		return this.jdbcTemplate.query(
				"select r.id as roleid, r.name from user u, role r, user_role ur " +
				"where ur.userid = u.id and ur.roleid = r.id and u.id = ? ", 
				new Object[]{userId},
				new RowMapperUtil.RoleMapper());
	}	
}
