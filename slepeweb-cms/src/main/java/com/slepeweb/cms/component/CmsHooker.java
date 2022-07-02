package com.slepeweb.cms.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.SiteService;

public class CmsHooker {
	@Autowired private SiteService siteService;
	
	private Map<String, ICmsHook> hooks = new HashMap<String, ICmsHook>();
	
	public void setHooks(Map<String, ICmsHook> map) {
		this.hooks = map;
	}
	
	public ICmsHook getHook(String siteName) {
		ICmsHook h = this.hooks.get(siteName);
		if (h == null) {
			h = new NoHook();
			this.hooks.put(siteName, h);
		}
		return h;
	}

	public ICmsHook getHook(Long siteId) {
		Site s = this.siteService.getSite(siteId);
		return getHook(s.getShortname());
	}
}
