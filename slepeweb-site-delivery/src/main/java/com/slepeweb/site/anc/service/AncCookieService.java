package com.slepeweb.site.anc.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.Site;

public interface AncCookieService {
	String ANC_COOKIE_PATH = "/";	
	
	List<ItemIdentifier> updateBreadcrumbsCookie(Item i, HttpServletRequest req, HttpServletResponse res);
	List<ItemIdentifier> getBreadcrumbsCookieValue(Site s, HttpServletRequest req);
	ItemIdentifier getLatestBreadcrumb(Site s, HttpServletRequest req);
	String getCookieValue(String name, HttpServletRequest req);
	void saveCookie(String name, String value, String path, HttpServletResponse res);
}
