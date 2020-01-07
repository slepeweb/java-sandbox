package com.slepeweb.cms.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slepeweb.cms.bean.SiteConfig;
import com.slepeweb.cms.service.SiteConfigService;

@Component
public class SiteConfiguration {
	
	@Autowired private SiteConfigService siteConfigService;
	
	private Map<Long, Properties> sites = new HashMap<Long, Properties>();
	
	public String getProperty(Long siteId, String key) {
		return getProperty(siteId, key, null);
	}
	
	public String getProperty(Long siteId, String key, String dflt) {
		Properties p = getProperties(siteId);
		String s = p.getProperty(key);
		if (StringUtils.isBlank(s) && dflt != null) {
			s = dflt;
		}
		
		return s;
	}
	
	public boolean getBooleanProperty(Long siteId, String key) {
		String s = getProperty(siteId, key);
		if (StringUtils.isBlank(s)) {
			return false;
		}
		
		return s.equals("true") || s.equals("1") || s.equals("yes");
	}
	
	public void setProperty(Long siteId, String key, String value) {
		Properties p = getProperties(siteId);
		if (p != null) {
			p.setProperty(key, value);
		}
	}
	
	private Properties getProperties(Long siteId) {
		Properties p = this.sites.get(siteId);
		if (p == null) {
			p = new Properties();
			for (SiteConfig sc : this.siteConfigService.getSiteConfigs(siteId)) {
				p.put(sc.getName(), sc.getValue());
			}
			this.sites.put(siteId, p);
		}
		return p;
	}
}
