package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Host.HostType;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class HostServiceImpl extends BaseServiceImpl implements HostService {
	
	private static Logger LOG = Logger.getLogger(HostServiceImpl.class);
	
	private final static String SELECT_TEMPLATE = 
			"select h.id, h.name, h.protocol, h.port, h.type, s.id as siteid, s.name as sitename, s.shortname, s.language, s.xlanguages " +
			"from host h " +
			"join site s on h.siteid = s.id " +
			"where %s";
	
	public Host save(Host h) {
		if (h.isDefined4Insert()) {
			Host dbRecord = getHost(h.getName());		
			if (dbRecord != null) {
				updateHost(dbRecord, h);
				return dbRecord;
			}
			else {
				insertHost(h);
			}
		}
		else {
			LOG.error(compose("Site not saved - insufficient data", h));
		}
		
		return h;
	}
	
	private Host insertHost(Host h) {		
		this.jdbcTemplate.update( "insert into host (name, port, type, protocol) values (?, ?, ?, ?)", 
				h.getName(), h.getPort(), h.getType(), h.getProtocol());		
		
		h.setId(getLastInsertId());			
		this.cacheEvictor.evict(h);
		LOG.info(compose("Added new host", h));		
		return h;
	}

	private void updateHost(Host dbRecord, Host h) {
		if (! dbRecord.equals(h)) {
			this.cacheEvictor.evict(dbRecord);
			dbRecord.assimilate(h);
			
			this.jdbcTemplate.update(
					"update host set name = ?, port = ?, type = ?, protocol = ? where id = ?", 
					dbRecord.getName(), dbRecord.getPort(), dbRecord.getType(), dbRecord.getProtocol(), dbRecord.getId());
			
			LOG.info(compose("Updated host", h));
		}
		else {
			h.setId(dbRecord.getId());
			LOG.info(compose("Host not modified", h));
		}
	}

	public void deleteHost(Host h) {
		if (this.jdbcTemplate.update("delete from host where id = ?", h.getId()) > 0) {
			LOG.warn(compose("Deleted host", h.getName()));
			this.cacheEvictor.evict(h);
		}
	}

	@Cacheable(value="serviceCache")
	public Host getHost(String name) {
		return getHost(String.format(SELECT_TEMPLATE, " h.name = ?"), new Object[]{name});
	}

	@Cacheable(value="serviceCache")
	public Host getHost(Long id) {
		return getHost(String.format(SELECT_TEMPLATE, " h.id = ?"), new Object[]{id});
	}
	
	private Host getHost(String sql, Object[] params) {
		return (Host) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.HostMapper()));
	}

	@Cacheable(value="serviceCache")
	public List<Host> getAllHosts(Long siteId) {
		return this.jdbcTemplate.query(
				String.format(SELECT_TEMPLATE, " h.siteid = ?"), new Object[] {siteId}, new RowMapperUtil.HostMapper());
	}


	@Cacheable(value="serviceCache")
	public List<Host> getHosts(Long siteId, HostType type) {
		return this.jdbcTemplate.query(
				String.format(SELECT_TEMPLATE, " h.siteid = ? and h.type = ?"), 
				new Object[] {siteId, type.name()}, 
				new RowMapperUtil.HostMapper());
	}
}
