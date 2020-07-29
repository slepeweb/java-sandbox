package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class UserServiceImpl extends BaseServiceImpl implements UserService {
	
	private static Logger LOG = Logger.getLogger(UserServiceImpl.class);
	
	private final static String SELECT_TEMPLATE = 
			"select u.user_id, u.name, u.alias, u.password, u.enabled " +
			"from user u " +
			"where %s";
	
	public User save(User u) {
		if (u.isDefined4Insert()) {
			User dbRecord = get(u.getAlias());		
			if (dbRecord != null) {
				update(dbRecord, u);
				return dbRecord;
			}
			else {
				insert(u);
			}
		}
		else {
			LOG.error(compose("User not saved - insufficient data", u));
		}
		
		return u;
	}
	
	private User insert(User u) {		
		this.jdbcTemplate.update( "insert into user (name, alias, password, enabled) values (?, ?, ?)", 
				u.getName(), u.getAlias(), u.getPassword(), u.isEnabled());	
		
		u.setId(getLastInsertId());			
		//this.cacheEvictor.evict(u);
		LOG.info(compose("Added new user", u));		
		return u;
	}

	private void update(User dbRecord, User u) {
		if (! dbRecord.equals(u)) {
			//this.cacheEvictor.evict(dbRecord);
			dbRecord.assimilate(u);
			
			this.jdbcTemplate.update(
					"update user set name = ?, alias = ?, password=?, enabled=? where user_id = ?", 
					dbRecord.getName(), dbRecord.getAlias(), dbRecord.getPassword(), dbRecord.isEnabled(), dbRecord.getId());
			
			LOG.info(compose("Updated user", u));
		}
		else {
			u.setId(dbRecord.getId());
			LOG.info(compose("User not modified", u));
		}
	}

	public void delete(Long id) {
		if (this.jdbcTemplate.update("delete from user where user_id = ?", id) > 0) {
			LOG.warn(compose("Deleted user", id));
			//this.cacheEvictor.evict(h);
		}
	}

	@Cacheable(value="serviceCache")
	public User get(String alias) {
		return get(String.format(SELECT_TEMPLATE, " u.alias = ?"), new Object[]{alias});
	}

	@Cacheable(value="serviceCache")
	public User get(Long id) {
		return get(String.format(SELECT_TEMPLATE, " u.user_id = ?"), new Object[]{id});
	}
	
	private User get(String sql, Object[] params) {
		return (User) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.UserMapper()));
	}

	public List<String> getRoles(Long userId) {
		return this.jdbcTemplate.queryForList(
				"select r.name from user u, role r, user_role ur " +
				"where ur.user_id = u.user_id and ur.role_id = r.role_id and u.user_id = ? ", 
				String.class,
				new Object[]{userId});
	}
}
