package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.AccessRule;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class AccessServiceImpl extends BaseServiceImpl implements AccessService {
	
	private static Logger LOG = Logger.getLogger(HostServiceImpl.class);
	
	public AccessRule save(AccessRule ar) {
		if (ar.isDefined4Insert()) {
			AccessRule dbRecord = get(ar.getId());		
			if (dbRecord != null) {
				update(dbRecord, ar);
				return dbRecord;
			}
			else {
				insert(ar);
			}
		}
		else {
			LOG.error(compose("Rule not saved - insufficient data", ar));
		}
		
		return ar;
	}
	
	private AccessRule insert(AccessRule ar) {		
		this.jdbcTemplate.update( "insert into access (siteid, name, role, tag, template, " + 
				"path, ownerid, access) values (?, ?, ?, ?, ?, ?, ?, ?)", 
				ar.getSiteId(), ar.getName(), ar.getRolePattern(), ar.getTagPattern(), 
				ar.getTemplatePattern(), ar.getItemPathPattern(), ar.getOwnerIdPattern(), ar.isAccess());	
		
		ar.setId(getLastInsertId());			
		//this.cacheEvictor.evict(ar);
		LOG.info(compose("Added new rule", ar));		
		return ar;
	}

	private void update(AccessRule dbRecord, AccessRule ar) {
		if (! dbRecord.equals(ar)) {
			//this.cacheEvictor.evict(dbRecord);
			dbRecord.assimilate(ar);
			
			this.jdbcTemplate.update(
					"update access set name=?, role=?, tag=?, template=?, path=?, ownerid=?, access=? where id=?", 
					dbRecord.getName(), dbRecord.getRolePattern(), dbRecord.getTagPattern(),
					dbRecord.getTemplatePattern(), dbRecord.getItemPathPattern(), 
					dbRecord.getOwnerIdPattern(), dbRecord.isAccess(), dbRecord.getId());
			
			LOG.info(compose("Updated Rule", ar));
		}
		else {
			ar.setId(dbRecord.getId());
			LOG.info(compose("Host not modified", ar));
		}
	}

	public void delete(Long id) {
		if (this.jdbcTemplate.update("delete from access where id = ?", id) > 0) {
			LOG.warn(compose("Deleted rule", id));
		}
	}

	public AccessRule get(Long siteId, String ruleName) {
		return (AccessRule) getFirstInList(this.jdbcTemplate.query("select * from access where siteid=? and name = ?", 
				new RowMapperUtil.AccessMapper(), siteId, ruleName));
	}

	public AccessRule get(Long id) {
		return (AccessRule) getFirstInList(this.jdbcTemplate.query("select * from access where id=?", 
				new RowMapperUtil.AccessMapper(), id));
	}
	
	private List<AccessRule> getList(Long siteId, String mode) {
		return this.jdbcTemplate.query(
				String.format("select * from access where siteid=? and mode=? and enabled=? order by name"), 
				new RowMapperUtil.AccessMapper(), siteId, mode, true);
	}
	
	public List<AccessRule> getReadable(Long siteId) {
		return getList(siteId, "r");
	}
	
	public List<AccessRule> getWriteable(Long siteId) {
		return getList(siteId, "w");
	}
}
