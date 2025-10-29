package com.slepeweb.site.service;

import com.slepeweb.cms.bean.Item;

public interface StaticSiteService {
	void build(Item target, String sessionId) throws Exception;
}
