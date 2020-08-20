package com.slepeweb.cms.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.utils.CookieHelper;

@Service
public class CookieServiceImpl extends CookieHelper implements CookieService {
	
	public void updateBreadcrumbsCookie(Item i, HttpServletRequest req, HttpServletResponse res) {
		super.updateBreadcrumbsCookie(CMS_COOKIE_PATH, i, req, res);
	}
	
	public String getRelativePositionCookieValue(HttpServletRequest req) {
		String pos = getCookieValue(RELATIVE_POSITION_NAME, req); 
		if (pos == null) {
			pos = "below";
		}
		return pos;
	}

}
