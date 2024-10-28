package com.slepeweb.cms.service;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.StickyAddNewControls;
import com.slepeweb.cms.utils.CookieHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CookieServiceImpl extends CookieHelper implements CookieService {
	
	public void updateBreadcrumbsCookie(Item i, HttpServletRequest req, HttpServletResponse res) {
		super.updateBreadcrumbsCookie(CMS_COOKIE_PATH, i, req, res);
	}
	
	public StickyAddNewControls getStickyAddNewControls(HttpServletRequest req) {
		String str = getCookieValue(STICKY_ADDNEW_CONTROLS, req); 
		return new StickyAddNewControls(str);
	}

}
