package com.slepeweb.site.util;

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
}
