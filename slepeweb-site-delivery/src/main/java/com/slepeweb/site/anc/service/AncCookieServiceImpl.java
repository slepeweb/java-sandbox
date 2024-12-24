package com.slepeweb.site.anc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.utils.CookieHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AncCookieServiceImpl extends CookieHelper implements AncCookieService {
	
	public List<ItemIdentifier> updateBreadcrumbsCookie(Item i, HttpServletRequest req, HttpServletResponse res) {
		
		ItemIdentifier targetKey = new ItemIdentifier(i.getOrigId());
		List<ItemIdentifier> breadcrumbs = getBreadcrumbsCookieValue(i.getSite(), req);	
		pushBreadcrumbs(breadcrumbs, targetKey);
		updateItemNames(breadcrumbs, i.getCmsService()); // We need item names in order to reorder existing matching entries
		saveCookie(getBreadcrumbsCookieName(i.getSite().getId()), join(breadcrumbs), ANC_COOKIE_PATH, res);
		return breadcrumbs;
	}
	
}
