package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.SiteConfigProperty;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class SiteConfigServiceImpl extends BaseServiceImpl implements SiteConfigService {
	
	private static Logger LOG = Logger.getLogger(SiteConfigServiceImpl.class);
	
	public SiteConfigProperty save(SiteConfigProperty sc) {
		if (sc.isDefined4Insert()) {
			SiteConfigProperty dbRecord = getSiteConfig(sc.getSiteId(), sc.getName());		
			if (dbRecord != null) {
				updateSiteConfig(dbRecord, sc);
				return dbRecord;
			}
			else {
				insertSiteConfig(sc);
			}
		}
		else {
			LOG.error(compose("SiteConfig not saved - insufficient data", sc));
		}
		
		return sc;
	}
	
	private void insertSiteConfig(SiteConfigProperty sc) {
		this.jdbcTemplate.update(
				"insert into config (siteid, name, value) values (?, ?, ?)", 
				sc.getSiteId(), sc.getName(), sc.getValue());
		
		LOG.info(compose("Added new site configuration property", sc));
	}

	private void updateSiteConfig(SiteConfigProperty dbRecord, SiteConfigProperty sc) {
		if (! dbRecord.equals(sc)) {
			dbRecord.assimilate(sc);
			
			this.jdbcTemplate.update(
					"update config set value = ? where siteid = ? and name = ?", 
					dbRecord.getValue(), dbRecord.getSiteId(), dbRecord.getName());
			
			LOG.info(compose("Updated site configuration property", sc));
		}
		else {
			LOG.info(compose("Site configuration property not modified", sc));
		}
	}
	
	public void deleteSiteConfig(SiteConfigProperty sc) {
		if (this.jdbcTemplate.update("delete from config where siteid = ? and name = ?", 
				sc.getSiteId(), sc.getName()) > 0) {
			LOG.warn(compose("Deleted site configuration property", String.valueOf(sc.getSiteId()), sc.getName()));
		}
	}

	public List<SiteConfigProperty> getAll() {
		return this.jdbcTemplate.query("select * from config", new RowMapperUtil.SiteConfigMapper());
	}
	
	public SiteConfigProperty getSiteConfig(Long siteId, String name) {
		return (SiteConfigProperty) getFirstInList(this.jdbcTemplate.query(
				"select * from config where siteid = ? and name = ?", 
				new RowMapperUtil.SiteConfigMapper(), siteId, name));
	}

	public List<SiteConfigProperty> getSiteConfigs(Long siteId) {
		return this.jdbcTemplate.query("select * from config where siteid = ? order by name", 
				new RowMapperUtil.SiteConfigMapper(), siteId);
	}
	
	public int getCount() {
		return this.jdbcTemplate.queryForObject("select count(*) from config", Integer.class);
	}
}
