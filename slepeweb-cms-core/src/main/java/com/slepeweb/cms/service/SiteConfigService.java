package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.SiteConfigProperty;


public interface SiteConfigService {
	void deleteSiteConfig(SiteConfigProperty sc);
	SiteConfigProperty getSiteConfig(Long siteId, String key);
	List<SiteConfigProperty> getAll();
	List<SiteConfigProperty> getSiteConfigs(Long siteId);
	SiteConfigProperty save(SiteConfigProperty sc);
	int getCount();
}
