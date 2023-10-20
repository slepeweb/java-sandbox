package com.slepeweb.cms.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.StickyAddNewControls;

public interface CookieService {
	static final String CMS_COOKIE_PATH = "/cms";	
	static final String STICKY_ADDNEW_CONTROLS = "stickyaddnew";	
	
	void updateBreadcrumbsCookie(Item i, HttpServletRequest req, HttpServletResponse res);
	List<ItemIdentifier> getBreadcrumbsCookieValue(Site s, HttpServletRequest req);
	String getCookieValue(String name, HttpServletRequest req);
	StickyAddNewControls getStickyAddNewControls(HttpServletRequest req);
	void saveCookie(String name, String value, String path, HttpServletResponse res);
}
