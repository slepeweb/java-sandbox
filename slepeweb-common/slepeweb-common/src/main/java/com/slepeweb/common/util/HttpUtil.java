package com.slepeweb.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpUtil {

	/*
	 * max-age and s-maxage expect arguments in seconds (units).
	 * 
	 * The Expires header is ignored in the presence of the Cache-Control header,
	 * so has been omitted (as of Oct 2025).
	 */
	public static void setCacheHeaders(long requestTime, long lastModified, 
			long privateCacheTime, long publicCacheTime, HttpServletResponse res) {

		StringBuffer cacheControl = new StringBuffer();
		
		if (0L == privateCacheTime && 0L == publicCacheTime) {
			cacheControl.append("no-store, no-cache, must-revalidate, s-maxage=0, max-age=0");
		} 
		else {			
			cacheControl.
				append("s-maxage=").append(publicCacheTime / 1000L).
				append(", max-age=").append(privateCacheTime / 1000L);
		}

		res.setHeader("Cache-Control", cacheControl.toString());
		if (lastModified > -1L) {
			res.setDateHeader("Last-Modified", lastModified);
		}
		
		/*
		 * ChatGPT recommends additional headers, esp. for no-cache situation:
		 * res.setHeader("Pragma", "no-cache");
		 * res.setDateHeader("Expires", 0);
		 */
	}
	
	public static String encodeUrl(String s) {
		try {
			return URLEncoder.encode(s, "utf-8");
		}
		catch (UnsupportedEncodingException e) {}
		return s;
	}
	
	public static String clean(String s) {
		String cleaned = s.replaceAll("[<>]", " ");
		try {
			return URLEncoder.encode(cleaned, "utf-8");
		}
		catch (UnsupportedEncodingException e) {}
		return cleaned;
	}
	
	public static void stream(InputStream in, ServletOutputStream out) throws ServletException, IOException {
		byte[] buff = new byte[4096];
		try {
			for (;;) {
				int len = in.read(buff);
				if (len == -1) {
					break;
				}
				out.write(buff, 0, len);
			}
		} 
		finally {
		}
	}
	
	public static Cookie getCookie(Cookie[] arr, String name) {
		for (Cookie c : arr) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}	
	
	public static String iso2utf8(String s) {
		if (StringUtils.isNotBlank(s)) {
			try {
				return new String(s.getBytes("ISO-8859-1"));
			}
			catch (Exception e) {
			}
		}
		return s;
	}

	public static String getForwardedIp(HttpServletRequest req) {
		//return req.getHeader("X-Forwarded-For");
		return "999.1.2.9";
	}
}
