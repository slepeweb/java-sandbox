package com.slepeweb.cms.test;

import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.LinkNameService;
import com.slepeweb.cms.service.LinkTypeService;
import com.slepeweb.cms.service.SiteService;

@Service
public class CacheTest extends BaseTest {
	private static Logger LOG = Logger.getLogger(CacheTest.class);
	
	@Autowired CacheManager cacheManager;
	@Autowired LinkTypeService linkTypeService;
	@Autowired LinkNameService linkNameService;
	@Autowired SiteService siteService;
	
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet("Media testbed").
			register(5010, "Check service cache is populated with binding LinkType");
		
		// 5010
		LinkType lt = this.linkTypeService.getLinkType("binding");		
		lt = this.linkTypeService.getLinkType("binding");
		lt = this.linkTypeService.getLinkType("binding");
		lt = this.linkTypeService.getLinkType("relation");
		lt = this.linkTypeService.getLinkType("relation");
		lt = this.linkTypeService.getLinkType("relation");
		
		Site s = this.siteService.getSite(TEST_SITE_NAME);
		@SuppressWarnings("unused")
		LinkName ln = this.linkNameService.getLinkName(s.getId(), lt.getId(), "std");
		ln = this.linkNameService.getLinkName(s.getId(), lt.getId(), "std");
		ln = this.linkNameService.getLinkName(s.getId(), lt.getId(), "std");
		
		Cache springCache = this.cacheManager.getCache("serviceCache");
		net.sf.ehcache.Cache ehCache = null;
		
		if (springCache != null) {
			Object obj = springCache.getNativeCache();
			
			if (obj instanceof net.sf.ehcache.Cache) {
				ehCache = (net.sf.ehcache.Cache) obj;
				
				for (Object key : ehCache.getKeys()) {
					LOG.debug(key.toString());
				}
				
				String key = "getLinkType-binding";
				Element elem = ehCache.get(key);
				
				if (elem != null) {
					Object o = elem.getObjectValue();
					r = trs.execute(5010);
					if (! (o instanceof LinkType)) {
						r.fail().setNotes("Failed to get a link type for key " + key);
					}
				}
			}
		}
		
		return trs;
	}
}
