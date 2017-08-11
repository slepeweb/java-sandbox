package com.slepeweb.site.util;

import javax.servlet.http.Cookie;

public class SiteUtil {
	
	public static Cookie getCookie(Cookie[] arr, String name) {
		for (Cookie c : arr) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}	
}
