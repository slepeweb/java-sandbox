package com.slepeweb.cms.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;

@Service
public class CookieServiceImpl implements CookieService {
	
	public final static String HISTORY_NAME_PREFIX = "history";
	public final static String COOKIE_PATH = "/cms";
	public final static int COOKIE_MAXAGE = 7 * 24 * 60 * 60;
	
	@Autowired private ItemService itemService;
	
	public void updateHistoryCookie(Item i, HttpServletRequest req, HttpServletResponse res) {
		
		ItemIdentifier targetKey = new ItemIdentifier(i.getOrigId());
		List<ItemIdentifier> nodeList = getHistoryCookieValue(i.getSite().getId(), req);
		
		if (nodeList.contains(targetKey)) {
			int index = nodeList.indexOf(targetKey);
			nodeList.remove(index);
		}
		
		if (nodeList.size() > 10) {
			nodeList.remove(nodeList.size() - 1);
		}
		
		nodeList.add(0, targetKey);
		saveCookie(getHistoryCookieName(i.getSite().getId()), StringUtils.join(nodeList, ","), res);
	}
	
	public List<ItemIdentifier> getHistoryCookieValue(long siteId, HttpServletRequest req) {
		
		for (Cookie c : req.getCookies()) {
			if (/*c.getPath() != null && c.getPath().equals(COOKIE_PATH) &&*/ 
					c.getName().equals(getHistoryCookieName(siteId))) {
				
				List<ItemIdentifier> list = new ArrayList<ItemIdentifier>();
				
				for (String s : c.getValue().split(",")) {
					list.add(new ItemIdentifier(s));
				}
				
				updateItemNames(list);
				return list;
			}
		}
		
		// Didn't find the cookie
		return new ArrayList<ItemIdentifier>();
	}
	
	private void saveCookie(String cookieName, String value, HttpServletResponse res) {
		Cookie c = new Cookie(cookieName, value);
		c.setPath(COOKIE_PATH);
		c.setMaxAge(COOKIE_MAXAGE);
		res.addCookie(c);
	}
	
	private String getHistoryCookieName(long siteId) {
		return String.format("%s-%d", HISTORY_NAME_PREFIX, siteId);
	}
	
	private void updateItemNames(List<ItemIdentifier> list) {
		Item h;		
		Iterator<ItemIdentifier> iter = list.iterator();
		ItemIdentifier ii;
		
		while (iter.hasNext()) {
			ii = iter.next();
			h = this.itemService.getEditableVersion(ii.getItemId());
			if (h != null) {
				ii.setName(StringUtils.abbreviate(h.getName(), 24));
			}
			else {
				iter.remove();
			}
		}
	}
	
}
