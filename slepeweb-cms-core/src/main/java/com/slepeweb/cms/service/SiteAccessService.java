package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.bean.User;

public interface SiteAccessService {
	boolean isAccessible(Item i);
	boolean isAccessible(SolrDocument4Cms doc, User u);
	void forceCacheRefresh();
}
