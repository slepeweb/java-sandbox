package com.slepeweb.cms.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.CmsService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class CookieHelper {
	
	public final static String BREADCRUMBS = "breadcrumbs";
	public static final String ID_SEPARATOR = "|";
	public static final String ID_REGEX = String.format("[\\%s]", ID_SEPARATOR);
	public final static int COOKIE_MAXAGE = 7 * 24 * 60 * 60;
	
	public void updateBreadcrumbsCookie(String cookiePath, Item i, HttpServletRequest req, HttpServletResponse res) {
		
		ItemIdentifier target = new ItemIdentifier(i.getOrigId());
		List<ItemIdentifier> breadcrumbs = getBreadcrumbsCookieValue(i.getSite(), req);	
		updateItemNames(breadcrumbs, i.getCmsService());
		pushBreadcrumbs(breadcrumbs, target);
		saveCookie(getBreadcrumbsCookieName(i.getSite().getId()), StringUtils.join(breadcrumbs, ID_SEPARATOR), cookiePath, res);
	}
	
	public String getBreadcrumbsCookieName(long siteId) {
		return String.format("%s-%d", BREADCRUMBS, siteId);
	}
	
	public ItemIdentifier getLatestBreadcrumb(Site s, HttpServletRequest req) {
		List<ItemIdentifier> list = getBreadcrumbsCookieValue(s, req);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public List<ItemIdentifier> getBreadcrumbsCookieValue(Site s, HttpServletRequest req) {
		
		String cookieName = getBreadcrumbsCookieName(s.getId());
		String cookieValue = getCookieValue(cookieName, req);		
		return parseBreadcrumbsString(cookieValue, s.getCmsService());
	}
	
	public void pushBreadcrumbs(List<ItemIdentifier> nodeList, ItemIdentifier target) {
		
		if (nodeList.contains(target)) {
			int index = nodeList.indexOf(target);
			nodeList.remove(index);
		}
		
		if (nodeList.size() > 10) {
			nodeList.remove(nodeList.size() - 1);
		}
		
		nodeList.add(0, target);
	}
	
	public List<ItemIdentifier> parseBreadcrumbsString(String cookieValue, CmsService cmsService) {
		
		if (cookieValue != null) {
			List<ItemIdentifier> list = new ArrayList<ItemIdentifier>();
			
			for (String s : cookieValue.split(ID_REGEX)) {
				list.add(new ItemIdentifier(s));
			}
			
			updateItemNames(list, cmsService);
			return list;
		}
		
		return new ArrayList<ItemIdentifier>();
	}
	
	public String getCookieValue(String name, HttpServletRequest req) {
		if (req.getCookies() != null) {
			for (Cookie c : req.getCookies()) {
				if (/*c.getPath() != null && c.getPath().equals(COOKIE_PATH) &&*/ c.getName().equals(name)) {
					return c.getValue();
				}
			}
		}
		
		return null;
	}
	
	public void saveCookie(String name, String value, String path, HttpServletResponse res) {
		Cookie c = new Cookie(name, value);
		c.setPath(path);
		c.setMaxAge(COOKIE_MAXAGE);
		res.addCookie(c);
	}
	
	protected void updateItemNames(List<ItemIdentifier> list, CmsService cmsService) {
		Item h;		
		Iterator<ItemIdentifier> iter = list.iterator();
		ItemIdentifier ii;
		
		while (iter.hasNext()) {
			ii = iter.next();
			h = cmsService.isEditorialContext() ? 
					cmsService.getItemService().getEditableVersion(ii.getItemId()) :
					cmsService.getItemService().getItem(ii.getItemId());
						
			if (h != null) {
				ii.
					setName(StringUtils.abbreviate(h.getName(), 24)).
					setPath(h.getUrl());
			}
			else {
				iter.remove();
			}
		}
	}
	
}
