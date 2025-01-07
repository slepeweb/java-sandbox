package com.slepeweb.money.filter;

import java.io.IOException;

import com.slepeweb.money.bean.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
		HttpServletResponse res = (HttpServletResponse) response;	
		User u = (User) req.getSession().getAttribute(User.USER_ATTR);
		
		if (	u == null && 
				! req.getServletPath().equals(this.loginPath) &&
				! req.getServletPath().startsWith("/resources")) {
			
			res.sendRedirect(req.getContextPath() + this.loginPath);
			return;
		}
		
		String[] accessibleRoles = new String[] {"browser", "editor", "admin"};
		
		if (u != null && ! u.hasRole(accessibleRoles)) {
			req.getSession().setAttribute(User.USER_ATTR, null);
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
			
		chain.doFilter(request, response);		
	}

}