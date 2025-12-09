package com.slepeweb.cms.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slepeweb.cms.service.SiteConfigService;

import jakarta.annotation.PostConstruct;

@Component
public class SiteConfigCache {
		
	@Autowired private SiteConfigService siteConfigService;
	
	private static Logger LOG = Logger.getLogger(SiteConfigCache.class);

	private Map<Long, SiteConfig> map;
	
	@PostConstruct
	private void init() {
		forceCacheRefresh();
	}
	
	public void put(Long siteId, SiteConfigProperty scp) {
		
		SiteConfig sc = this.map.get(siteId);
		if (sc == null) {
			sc = new SiteConfig(siteId);
			this.map.put(siteId, sc);
		}
		
		sc.put(scp.getName(), scp);
	}

	public String getValue(Long siteId, String name) {		
		return getValue(siteId, name, null);
	}
	
	public String getValue(Long siteId, String name, String dflt) {	
		SiteConfig sc = get(siteId);
		String value = null;
		
		if (sc != null) {
			value = sc.get(name);
		}
		
		// siteId == 0 represents global configs
		if (value == null && siteId.longValue() != 0L) {
			sc = get(0L);
			value = sc.get(name);
		}
		
		return value != null ? value : dflt;
	}
	
	public Integer getIntValue(Long siteId, String name, Integer dflt) {
		String s = getValue(siteId, name);
		return StringUtils.isNumeric(s) ? Integer.valueOf(s) : dflt;
	}
	
	public Boolean getBooleanValue(Long siteId, String name, Boolean dflt) {
		String s = getValue(siteId, name);
		return s != null && s.toLowerCase().matches("yes|true|1");
	}
	
	public SiteConfig get(Long siteId) {
		return this.map.get(siteId);
	}
	
	public void forceCacheRefresh() {
		this.map = new HashMap<Long, SiteConfig>();
		
		for (SiteConfigProperty scp : this.siteConfigService.getAll()) {
			put(scp.getSiteId(), scp);
		}
		
		LOG.info("(Re-)built the site config properties cache");
	}
	
		
	public static class SiteConfig {
		
		private Long siteId;
		private Properties props;
		
		public SiteConfig(Long siteId) {
			this.siteId = siteId;
			this.props = new Properties();
		}
		
		public void put(String name, SiteConfigProperty prop) {
			this.props.setProperty(name, prop.getValue());
		}

		public String get(String name) {
			return this.props.getProperty(name);
		}
		
		public Long getSiteId() {
			return this.siteId;
		}
	}
}


