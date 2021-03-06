package com.slepeweb.cms.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.Site;

public interface CookieService {
	String CMS_COOKIE_PATH = "/cms";	
	String RELATIVE_POSITION_NAME = "relativep";
	
	void updateBreadcrumbsCookie(Item i, HttpServletRequest req, HttpServletResponse res);
	List<ItemIdentifier> getBreadcrumbsCookieValue(Site s, HttpServletRequest req);
	String getCookieValue(String name, HttpServletRequest req);
	String getRelativePositionCookieValue(HttpServletRequest req);
	void saveCookie(String name, String value, String path, HttpServletResponse res);
}
