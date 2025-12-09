package com.slepeweb.cms.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slepeweb.cms.bean.SiteConfigCache;
import com.slepeweb.cms.service.SiteAccessService;

/*
 * Utility to refresh caches, eg SiteConfigCache and SiteAccess cache
 */

@Component
public class CacheRefresher {
	
	@Autowired private SiteConfigCache siteConfigCache;
	@Autowired private SiteAccessService siteAccessService;

	public void execute() {
		this.siteConfigCache.forceCacheRefresh();
		this.siteAccessService.forceCacheRefresh();
	}
}
