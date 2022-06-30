package com.slepeweb.cms.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.SiteService;

public class CmsHooker {
	@Autowired private SiteService siteService;
	
	private List<ICmsHook> hooks = new ArrayList<ICmsHook>();
	
	public void setHooks(List<ICmsHook> list) {
		this.hooks = list;
	}
	
	/* 
	 * Not expecting there to be too many sites (ie < 10), so leaving collection as list,
	 * instead of converting to a map.
	 */
	public ICmsHook getHook(String siteName) {
		for (ICmsHook h : this.hooks) {
			if (h.getSitename().equals(siteName)) {
				return h;
			}
		}
		return null;
	}

	public ICmsHook getHook(Long siteId) {
		Site s = this.siteService.getSite(siteId);
		for (ICmsHook h : this.hooks) {
			if (h.getSitename().equals(s.getShortname())) {
				return h;
			}
		}
		return null;
	}
}
