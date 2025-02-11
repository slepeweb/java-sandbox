package com.slepeweb.money.filter;

import java.io.IOException;
import java.util.Calendar;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CacheingFilter implements Filter {
	
	private static final String CACHE_CONTROL = "Cache-Control";
	private static final String EXPIRES = "Expires";
	
	// Default cache time, in seconds
	private int cacheTime = 0;

	public void init(FilterConfig config) throws ServletException {
		String value;

		if ((value = config.getInitParameter("cacheTime")) != null) {
			this.cacheTime = Integer.parseInt(value);
		}
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
				
		HttpServletRequest req = (HttpServletRequest) request;		
		HttpServletResponse res = (HttpServletResponse) response;		
		chain.doFilter(request, response);
		
		/* 
		 * Set missing cacheing headers if not present, unless the status code is 302.
		 * This applies to css and js files only.
		 */
		if (req.getServletPath().endsWith(".js") || req.getServletPath().endsWith(".css")) {
			if (! res.containsHeader(CACHE_CONTROL) && res.getStatus() != HttpServletResponse.SC_NOT_MODIFIED) {
				if (this.cacheTime > 0) {
					res.setHeader(CACHE_CONTROL, 
							"max-age=" + this.cacheTime + ", s-maxage=" + this.cacheTime);
				}
				else {
					res.setHeader(CACHE_CONTROL, "no-store, no-cache");
				}
			}
			
			if (! res.containsHeader(EXPIRES) && res.getStatus() != HttpServletResponse.SC_NOT_MODIFIED) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.SECOND, this.cacheTime);
				res.setDateHeader(EXPIRES, cal.getTime().getTime());
			}
		}
	}
}