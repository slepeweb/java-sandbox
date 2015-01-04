package com.slepeweb.site.servlet;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class ResponseHeaderFilter implements Filter {
	// Default cache time, in seconds
	private int cacheTime = 0;
	private String charSet = null;

	public void init(FilterConfig config) throws ServletException {
		String value;

		if ((value = config.getInitParameter("cacheTime")) != null) {
			this.cacheTime = Integer.parseInt(value);
		}

		if ((value = config.getInitParameter("charSet")) != null) {
			this.charSet = value;
		}
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		
		HttpServletResponse resp = (HttpServletResponse) response;

		if (request instanceof HttpServletRequest) {
			Calendar cal = Calendar.getInstance();

			if (this.cacheTime > 0) {
				// Set to expire later
				cal.add(Calendar.SECOND, this.cacheTime);
				resp.setDateHeader("Expires", cal.getTime().getTime());
				resp.setHeader("Cache-Control", 
						"max-age=" + this.cacheTime + ", s-maxage=" + this.cacheTime);
			} else {
				// Set to expire far in the past.
				resp.setDateHeader("Expires", 0);
				resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

				// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
				resp.addHeader("Cache-Control", "post-check=0, pre-check=0");

				// Set standard HTTP/1.0 no-cache header.
				resp.setHeader("Pragma", "no-cache");
			}

			/* 
			 * Default to text/html - should the request be for a binary type,
			 * then CmsDeliveryServlet will overwrite this value.
			 */
			if (StringUtils.isNotBlank(this.charSet)) {
				response.setContentType("text/html;charset=" + this.charSet);
			}
		}

		chain.doFilter(request, response);
	}

}