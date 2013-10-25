package com.slepeweb.sandbox.acm.mvc.bean;

import org.springframework.web.context.support.WebApplicationObjectSupport;

/**
 * Class to set and get objects from servlet context
 * 
 * @author Amit Viroja
 * 
 */
public class WebAppBean extends WebApplicationObjectSupport {
	public void setAttribute(String key, Object value) {
		getServletContext().setAttribute(key, value);
	}

	public Object getAttribute(String key) {
		return getServletContext().getAttribute(key);
	}

	public String getInitParam(String paramName) {
		return getServletContext().getInitParameter(paramName);
	}
}
