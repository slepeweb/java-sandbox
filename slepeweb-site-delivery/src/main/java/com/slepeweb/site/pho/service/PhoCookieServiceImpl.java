package com.slepeweb.site.pho.service;

import org.springframework.stereotype.Service;

import com.slepeweb.site.pho.bean.PhoCookieValues;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class PhoCookieServiceImpl implements PhoCookieService {

	@Override
	public String getLastSearchText(HttpServletRequest req) {
		return getCookieValue(SEARCH_TERMS, req);
	}

	@Override
	public String getLastFromDate(HttpServletRequest req) {
		return getCookieValue(SEARCH_FROM, req);
	}

	@Override
	public String getLastToDate(HttpServletRequest req) {
		return getCookieValue(SEARCH_TO, req);
	}

	@Override
	public void saveLastSearchText(String value, HttpServletResponse res) {
		saveCookie(PhoCookieService.SEARCH_TERMS, value, res);
	}

	@Override
	public void saveLastFromDate(String value, HttpServletResponse res) {
		saveCookie(PhoCookieService.SEARCH_FROM, value, res);
	}

	@Override
	public void saveLastToDate(String value, HttpServletResponse res) {
		saveCookie(PhoCookieService.SEARCH_TO, value, res);
	}
	
	private void saveCookie(String name, String value, HttpServletResponse res) {
		Cookie c = new Cookie(name, value);
		c.setPath(PhoCookieService.COOKIE_PATH);
		c.setMaxAge(PhoCookieService.COOKIE_MAXAGE);
		res.addCookie(c);
	}
	
	private String getCookieValue(String name, HttpServletRequest req) {
		if (req.getCookies() != null) {
			for (Cookie c : req.getCookies()) {
				if (/*c.getPath() != null && c.getPath().equals(COOKIE_PATH) &&*/ c.getName().equals(name)) {
					return c.getValue();
				}
			}
		}
		
		return null;
	}

	@Override
	public PhoCookieValues getAllCookieValues(HttpServletRequest req) {
		return new PhoCookieValues().
				setText(getLastSearchText(req)).
				setFrom(getLastFromDate(req)).
				setTo(getLastToDate(req));

	}

	@Override
	public void saveAllCookieValues(PhoCookieValues v, HttpServletResponse res) {
		saveLastSearchText(v.getText(), res);
		saveLastFromDate(v.getFrom(), res);
		saveLastToDate(v.getTo(), res);
	}
}
