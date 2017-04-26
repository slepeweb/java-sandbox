package com.slepeweb.cms.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.SiteConfig;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class SiteConfigServiceImpl extends BaseServiceImpl implements SiteConfigService {
	
	private static Logger LOG = Logger.getLogger(SiteConfigServiceImpl.class);
	
	public SiteConfig save(SiteConfig sc) {
		if (sc.isDefined4Insert()) {
			SiteConfig dbRecord = getSiteConfig(sc.getSiteId(), sc.getName());		
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
	
	private void insertSiteConfig(SiteConfig sc) {
		this.jdbcTemplate.update(
				"insert into config (siteid, name, value) values (?, ?, ?)", 
				sc.getSiteId(), sc.getName(), sc.getValue());
		
		this.cacheEvictor.evict(sc);
		LOG.info(compose("Added new site configuration property", sc));
	}

	private void updateSiteConfig(SiteConfig dbRecord, SiteConfig sc) {
		if (! dbRecord.equals(sc)) {
			this.cacheEvictor.evict(dbRecord);
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
	
	public void deleteSiteConfig(SiteConfig sc) {
		if (this.jdbcTemplate.update("delete from config where siteid = ? and name = ?", 
				sc.getSiteId(), sc.getName()) > 0) {
			LOG.warn(compose("Deleted site configuration property", String.valueOf(sc.getSiteId()), sc.getName()));
			this.cacheEvictor.evict(sc);
		}
	}

	@Cacheable(value="serviceCache")
	public SiteConfig getSiteConfig(Long siteId, String name) {
		return (SiteConfig) getFirstInList(this.jdbcTemplate.query(
				"select * from config where siteid = ? and name = ?", 
				new Object[]{siteId, name}, 
				new RowMapperUtil.SiteConfigMapper()));
	}

	@Cacheable(value="serviceCache")
	public List<SiteConfig> getSiteConfigs(Long siteId) {
		return this.jdbcTemplate.query("select * from config where siteid = ? order by name", 
				new Object[]{}, new RowMapperUtil.SiteConfigMapper());
	}
	
	@SuppressWarnings("deprecation")
	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from config");
	}

	@Cacheable(value="serviceCache")
	public String getProperty(Long siteId, String name, String dflt) {
		SiteConfig config = getSiteConfig(siteId, name);
		if (config != null) {
			return config.getValue();
		}
		return dflt;
	}

	public String getProperty(Long siteId, String name) {
		return getProperty(siteId, name, null);
	}
	
	public Integer getIntegerProperty(Long siteId, String name, Integer dflt) {
		String value = getProperty(siteId, name, String.valueOf(dflt));
		if (value == null) {
			return dflt;
		}
		
		if (StringUtils.isNumeric(value)) {
			return Integer.valueOf(value);
		}
		return null;
	}

	public Integer getIntegerProperty(Long siteId, String name) {
		return getIntegerProperty(siteId, name, null);
	}
}
