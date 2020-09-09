package com.slepeweb.cms.service;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Role;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class RoleServiceImpl extends BaseServiceImpl implements RoleService {
	
	private static Logger LOG = Logger.getLogger(RoleServiceImpl.class);
	
	public Role save(Role r) {
		if (r.isDefined4Insert()) {
			Role dbRecord = get(r.getName());		
			if (dbRecord != null) {
				update(dbRecord, r);
				return dbRecord;
			}
			else {
				insert(r);
			}
		}
		else {
			LOG.error(compose("Role not saved - insufficient data", r));
		}
		
		return r;
	}
	
	private Role insert(Role r) {		
		this.jdbcTemplate.update( "insert into role name=?", r.getName());	
		
		r.setId(getLastInsertId());			
		//this.cacheEvictor.evict(u);
		LOG.info(compose("Added new role", r));		
		return r;
	}

	private void update(Role dbRecord, Role r) {
		if (! dbRecord.equals(r)) {
			//this.cacheEvictor.evict(dbRecord);
			dbRecord.assimilate(r);
			
			this.jdbcTemplate.update(
					"update role set name=? where role_id=?", 
					dbRecord.getName(), dbRecord.getId());
			
			LOG.info(compose("Updated role", r));
		}
		else {
			r.setId(dbRecord.getId());
			LOG.info(compose("Role not modified", r));
		}
	}

	public void delete(Long id) {
		if (this.jdbcTemplate.update("delete from role where role_id = ?", id) > 0) {
			LOG.warn(compose("Deleted role", id));
			//this.cacheEvictor.evict(h);
		}
	}

	@Cacheable(value="serviceCache")
	public Role get(String name) {
		return (Role) getFirstInList(this.jdbcTemplate.query(
				"select * from role where name = ?", new Object[]{name}, new RowMapperUtil.RoleMapper()));
	}

	@Override
	public Role get(Long id) {
		return (Role) getFirstInList(this.jdbcTemplate.query(
				"select * from role where role_id = ?", new Object[]{id}, new RowMapperUtil.RoleMapper()));
	}
}
