package com.slepeweb.cms.component;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slepeweb.cms.bean.SiteConfigCache;
import com.slepeweb.cms.service.SiteAccessService;

/*
 * Utility to refresh caches, eg SiteConfigCache and SiteAccess cache
 */

@Component
public class CacheRefresher {
	
	private static Logger LOG = Logger.getLogger(CacheRefresher.class);
	
	@Autowired private SiteConfigCache siteConfigCache;
	@Autowired private SiteAccessService siteAccessService;
	
	private int maxRefreshesInPeriod = 2;
	private long periodResetInterval = 5 * 60 * 1000;
	private long periodStart = System.currentTimeMillis();
	private int refreshCount = 0;

	public synchronized void execute() {
		long now = System.currentTimeMillis();
		if ((now - this.periodStart) > this.periodResetInterval) {
			// Sufficient time has passed to reset monitoring
			this.periodStart = now;
			this.refreshCount = 0;
		}
		else if (this.refreshCount >= this.maxRefreshesInPeriod) {
			// We are within a monitoring session, and the number of refreshes has hit the max
			LOG.warn("Too many cache-refresh requests - Ignored");
			return;
		}
		
		this.refreshCount++;
		this.siteConfigCache.forceCacheRefresh();
		this.siteAccessService.forceCacheRefresh();
	}
}
