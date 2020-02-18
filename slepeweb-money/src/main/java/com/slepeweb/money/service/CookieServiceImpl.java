package com.slepeweb.money.service;

import java.sql.Timestamp;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.slepeweb.money.Util;

@Service
public class CookieServiceImpl implements CookieService {
	
	public final static String ACCOUNT_COOKIE_NAME = "accountid";
	public final static String LAST_ENTERED_COOKIE_NAME = "lastentered";
	public final static String COOKIE_PATH = "/money";
	public final static int COOKIE_MAXAGE = 7 * 24 * 60 * 60;
	
	public void updateAccountCookie(long accountId, HttpServletRequest req, HttpServletResponse res) {
		saveCookie(ACCOUNT_COOKIE_NAME, String.valueOf(accountId), res);
	}
	
	public Long getAccountId(HttpServletRequest req) {
		
		for (Cookie c : req.getCookies()) {
			if (c.getName().equals(ACCOUNT_COOKIE_NAME)) {
				return Long.valueOf(c.getValue());
			}
		}
		
		// Didn't find the cookie
		return null;
	}
	
	public void updateLastEnteredCookie(Timestamp t, HttpServletRequest req, HttpServletResponse res) {
		saveCookie(LAST_ENTERED_COOKIE_NAME, Util.formatTimestamp(t), res);
	}
	
	public Timestamp getLastEntered(HttpServletRequest req) {
		
		for (Cookie c : req.getCookies()) {
			if (c.getName().equals(LAST_ENTERED_COOKIE_NAME)) {
				return Util.parseTimestamp(c.getValue());
			}
		}
		
		// Didn't find the cookie
		return null;
	}
	
	private void saveCookie(String cookieName, String value, HttpServletResponse res) {
		Cookie c = new Cookie(cookieName, value);
		c.setPath(COOKIE_PATH);
		c.setMaxAge(COOKIE_MAXAGE);
		res.addCookie(c);
	}		
}
