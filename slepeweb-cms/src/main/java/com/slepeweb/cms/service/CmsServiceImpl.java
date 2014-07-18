package com.slepeweb.cms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;

@Service
public class CmsServiceImpl implements CmsService {
	
	@Autowired
	private SiteService siteService;
	
	@Autowired
	private ItemService itemService;
	
	public Site getSite(String name) {
		return this.siteService.getSite(name);
	}
	
	public Site getSite(Long id) {
		return this.siteService.getSite(id);
	}

	public void addSite(Site s) {
		this.siteService.addSite(s);
	}

	public Item getItem(Long siteId, String path) {
		return this.itemService.getItem(siteId, path);
	}
	
}
