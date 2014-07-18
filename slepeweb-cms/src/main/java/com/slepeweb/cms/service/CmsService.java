package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;

public interface CmsService {
	void addSite(Site s);
	Site getSite(String name);
	Site getSite(Long id);
	Item getItem(Long siteId, String path);
}
