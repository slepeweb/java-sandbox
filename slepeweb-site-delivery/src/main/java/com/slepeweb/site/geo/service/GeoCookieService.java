package com.slepeweb.site.geo.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.Site;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface GeoCookieService {
	String GEO_COOKIE_PATH = "/";	
	
	List<ItemIdentifier> updateBreadcrumbsCookie(Item i, HttpServletRequest req, HttpServletResponse res);
	List<ItemIdentifier> getBreadcrumbsCookieValue(Site s, HttpServletRequest req);
	ItemIdentifier getLatestBreadcrumb(Site s, HttpServletRequest req);
	String getCookieValue(String name, HttpServletRequest req);
	void saveCookie(String name, String value, String path, HttpServletResponse res);
}
