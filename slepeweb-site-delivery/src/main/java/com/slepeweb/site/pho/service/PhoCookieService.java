package com.slepeweb.site.pho.service;

import com.slepeweb.site.pho.bean.PhoCookieValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PhoCookieService {
	String COOKIE_PATH = "/";	
	int COOKIE_MAXAGE = 3600;
	String COOKIE_NAME = "phoSearchTerms";
	
	void saveCookie(PhoCookieValues v, HttpServletResponse res);
	PhoCookieValues getCookieValues(HttpServletRequest req);
}
