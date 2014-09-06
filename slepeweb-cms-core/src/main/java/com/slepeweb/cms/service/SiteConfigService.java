package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.SiteConfig;


public interface SiteConfigService {
	void deleteSiteConfig(SiteConfig sc);
	SiteConfig getSiteConfig(Long siteId, String key);
	List<SiteConfig> getSiteConfigs(Long siteId);
	SiteConfig save(SiteConfig sc);
	int getCount();
}
