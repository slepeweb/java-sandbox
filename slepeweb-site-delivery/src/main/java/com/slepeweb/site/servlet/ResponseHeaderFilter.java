package com.slepeweb.site.servlet;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ResponseHeaderFilter implements Filter {
	
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
		
		HttpServletResponse resp = (HttpServletResponse) response;
		
		chain.doFilter(request, response);
		
		// Set missing cacheing headers if not present, unless the status code is 302
		if (! resp.containsHeader(CACHE_CONTROL) && resp.getStatus() != HttpServletResponse.SC_NOT_MODIFIED) {
			if (this.cacheTime > 0) {
				resp.setHeader(CACHE_CONTROL, 
						"max-age=" + this.cacheTime + ", s-maxage=" + this.cacheTime);
			}
			else {
				resp.setHeader(CACHE_CONTROL, "no-store, no-cache");
			}
		}
		
		if (! resp.containsHeader(EXPIRES) && resp.getStatus() != HttpServletResponse.SC_NOT_MODIFIED) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, this.cacheTime);
			resp.setDateHeader(EXPIRES, cal.getTime().getTime());
		}
	}

}