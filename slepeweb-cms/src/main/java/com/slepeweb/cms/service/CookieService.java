package com.slepeweb.cms.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;

public interface CookieService {
	String RELATIVE_POSITION_NAME = "relativep";
	void saveCookie(String cookieName, String value, HttpServletResponse res);
	void updateHistoryCookie(Item i, HttpServletRequest req, HttpServletResponse res);
	List<ItemIdentifier> getHistoryCookieValue(long siteId, HttpServletRequest req);
	String getCookieValue(String name, HttpServletRequest req);
	String getRelativePositionCookieValue(HttpServletRequest req);
}
