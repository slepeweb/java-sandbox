package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.StickyAddNewControls;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
	static final String CMS_COOKIE_PATH = "/cms_";	
	static final String STICKY_ADDNEW_CONTROLS = "stickyaddnew";	
	
	void updateBreadcrumbsCookie(Item i, HttpServletRequest req, HttpServletResponse res);
	List<ItemIdentifier> getBreadcrumbsCookieValue(Site s, HttpServletRequest req);
	String getCookieValue(String name, HttpServletRequest req);
	StickyAddNewControls getStickyAddNewControls(HttpServletRequest req);
	void saveCookie(String name, String value, String path, HttpServletResponse res);
}
