package com.slepeweb.site.anc.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.utils.CookieHelper;

@Service
public class AncCookieServiceImpl extends CookieHelper implements AncCookieService {
	
	public void updateBreadcrumbsCookie(Item i, HttpServletRequest req, HttpServletResponse res) {
		
		ItemIdentifier targetKey = new ItemIdentifier(i.getOrigId());
		List<ItemIdentifier> breadcrumbs = getBreadcrumbsCookieValue(i.getSite(), req);	
		updateItemNames(breadcrumbs, i.getCmsService().getItemService());
		pushBreadcrumbs(breadcrumbs, targetKey);
		saveCookie(getBreadcrumbsCookieName(i.getSite().getId()), StringUtils.join(breadcrumbs, ","), ANC_COOKIE_PATH, res);
	}
	
}
