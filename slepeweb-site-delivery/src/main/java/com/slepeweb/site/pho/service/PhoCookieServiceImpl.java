package com.slepeweb.site.pho.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.slepeweb.site.pho.bean.PhoCookieValues;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class PhoCookieServiceImpl implements PhoCookieService {

	@Override
	public PhoCookieValues getCookieValues(HttpServletRequest req) {
		if (req.getCookies() != null) {
			for (Cookie c : req.getCookies()) {
				if (/*c.getPath() != null && c.getPath().equals(COOKIE_PATH) &&*/ c.getName().equals(COOKIE_NAME)) {
					try {
						String value = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8.name());
						return new PhoCookieValues(value);
					}
					catch (UnsupportedEncodingException e) {
					}
				}
			}
		}
		
		return new PhoCookieValues();
	}

	@Override
	public void saveCookie(PhoCookieValues cv, HttpServletResponse res) {
		try {
			Cookie c = new Cookie(COOKIE_NAME, URLEncoder.encode(cv.toString(), StandardCharsets.UTF_8.name()));
			c.setPath(PhoCookieService.COOKIE_PATH);
			c.setMaxAge(PhoCookieService.COOKIE_MAXAGE);
			res.addCookie(c);
		}
		catch (UnsupportedEncodingException e ) {}
	}
}
