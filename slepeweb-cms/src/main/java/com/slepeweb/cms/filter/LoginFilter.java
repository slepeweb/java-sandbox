package com.slepeweb.cms.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginFilter implements Filter {
	
	private String loginPath;
	
	public void init(FilterConfig config) throws ServletException {
		this.loginPath = config.getInitParameter("loginPath");
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
				
		HttpServletRequest req = (HttpServletRequest) request;	
		if (req.getSession().getAttribute("_user") == null && 
				! req.getServletPath().equals(this.loginPath) &&
				! req.getServletPath().startsWith("/resources")) {
			
			HttpServletResponse res = (HttpServletResponse) response;	
			req.getRequestDispatcher(this.loginPath).forward(req, res);;
			return;
		}
		
		chain.doFilter(request, response);		
	}

}