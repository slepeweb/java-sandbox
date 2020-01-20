package com.slepeweb.cms.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;

public interface CookieService {
	void updateHistoryCookie(Item i, HttpServletRequest req, HttpServletResponse res);
	List<ItemIdentifier> getHistoryCookieValue(long siteId, HttpServletRequest req);
}
