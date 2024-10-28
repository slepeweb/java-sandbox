package com.slepeweb.site.pho.service;

import com.slepeweb.site.pho.bean.PhoCookieValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PhoCookieService {
	String COOKIE_PATH = "/";	
	int COOKIE_MAXAGE = 3600;
	String SEARCH_TERMS = "searchTerms";
	String SEARCH_FROM = "fromDate";
	String SEARCH_TO = "toDate";
	
	String getLastSearchText(HttpServletRequest req);
	String getLastFromDate(HttpServletRequest req);
	String getLastToDate(HttpServletRequest req);
	PhoCookieValues getAllCookieValues(HttpServletRequest req);
	
	void saveLastSearchText(String value, HttpServletResponse res);
	void saveLastFromDate(String value, HttpServletResponse res);
	void saveLastToDate(String value, HttpServletResponse res);
	void saveAllCookieValues(PhoCookieValues v, HttpServletResponse res);
}
