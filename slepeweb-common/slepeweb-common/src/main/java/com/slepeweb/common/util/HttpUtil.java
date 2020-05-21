package com.slepeweb.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class HttpUtil {

	public static void setCacheHeaders(long requestTime, long lastModified, 
			long privateCacheTime, long publicCacheTime, HttpServletResponse res) {

		long expireTime;
		StringBuffer cacheControl = new StringBuffer();
		
		if (0L == privateCacheTime && 0L == publicCacheTime) {
			expireTime = requestTime;
			cacheControl.append("no-cache, s-maxage=0, max-age=0");
		} 
		else {			
			cacheControl.
				append("s-maxage=").append(publicCacheTime / 1000L).
				append(", max-age=").append(privateCacheTime / 1000L);
			expireTime = requestTime + publicCacheTime;
		}

		res.setHeader("Cache-Control", cacheControl.toString());
		res.setDateHeader("Expires", expireTime);
		if (lastModified > -1L) {
			res.setDateHeader("Last-Modified", lastModified);
		}
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
}
