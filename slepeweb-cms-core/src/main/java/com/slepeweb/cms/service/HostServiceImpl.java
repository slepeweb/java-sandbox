package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Host.Deployment;
import com.slepeweb.cms.bean.Host.HostType;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class HostServiceImpl extends BaseServiceImpl implements HostService {
	
	private static Logger LOG = Logger.getLogger(HostServiceImpl.class);
	
	private final static String SELECT_TEMPLATE = 
			"select h.id, h.name, h.protocol, h.port, h.type, h.deployment, s.id as siteid, s.name as sitename, s.shortname, s.language, s.xlanguages, s.secured " +
			"from host h " +
			"join site s on h.siteid = s.id " +
			"where %s";
	
	public Host save(Host h) {
		if (h.isDefined4Insert()) {
			Host dbRecord = getHost(h.getName(), h.getPort(), h.getType());		
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
		this.jdbcTemplate.update( "insert into host (name, port, type, deployment, protocol) values (?, ?, ?, ?, ?)", 
				h.getName(), h.getPort(), h.getType(), h.getDeployment(), h.getProtocol());		
		
		h.setId(getLastInsertId());			
		LOG.info(compose("Added new host", h));		
		return h;
	}

	private void updateHost(Host dbRecord, Host h) {
		if (! dbRecord.equals(h)) {
			dbRecord.assimilate(h);
			
			this.jdbcTemplate.update(
					"update host set name = ?, port = ?, type = ?, deployment = ?, protocol = ? where id = ?", 
					dbRecord.getName(), dbRecord.getPort(), dbRecord.getType(), dbRecord.getDeployment(), 
					dbRecord.getProtocol(), dbRecord.getId());
			
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
		}
	}
	
	public Host getHost(Long id) {
		return getFirstHost(String.format(SELECT_TEMPLATE, " h.id = ?"), id);
	}
	
	/*
	 *  One host could serve both editorial AND delivery webapps, resulting in 2 db records.
	 *  This method will return the first, and so should only be used to identify the site (Host.getSite()).
	 */	
	public Host getHost(String name, int port) {
		return getFirstHost(
				String.format(SELECT_TEMPLATE, " h.name = ? and h.port = ?"), name, port);
	}

	// This returns a specific host record - there should only be one, due to db constraints.
	public Host getHost(String name, int port, HostType type) {
		return getFirstHost(
				String.format(SELECT_TEMPLATE, " h.name = ? and h.port = ? and h.type = ?"), name, port, type.name());
	}

	/*
	 * The db has a unique key constraint for (siteid, hostType, deployment)
	 */
	public Host getHost(Long siteId, HostType type, Deployment deployment) {
		return getFirstHost(
				String.format(SELECT_TEMPLATE, " h.siteid = ? and h.type = ? and h.deployment = ?"), siteId, type.name(), deployment.name());
	}

	private Host getFirstHost(String sql, Object... params) {
		return (Host) getFirstInList(this.jdbcTemplate.query(
			sql, new RowMapperUtil.HostMapper(), params));
	}

	public List<Host> getHosts(Long siteId) {
		return this.jdbcTemplate.query(
				String.format(SELECT_TEMPLATE, " h.siteid = ?"), new RowMapperUtil.HostMapper(), siteId);
	}


	public List<Host> getHosts(Long siteId, HostType type) {
		return this.jdbcTemplate.query(
				String.format(SELECT_TEMPLATE, " h.siteid = ? and h.type = ?"), 
				new RowMapperUtil.HostMapper(),
				siteId, type.name());
	}
}
