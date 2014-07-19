package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Site;


public interface SiteService {
	void insertSite(Site s);
	void updateSite(Site s);
	void deleteSite(Long id);
	void deleteSite(Site s);
	Site getSite(String name);
	Site getSite(Long id);
}
