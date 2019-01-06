package com.slepeweb.money.control;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class BaseController {
	private static Logger LOG = Logger.getLogger(BaseController.class);
	public static final String USER = "_user";
	
	@ModelAttribute(value=USER)
	protected User getUser(@AuthenticationPrincipal User u) {
		LOG.trace(String.format("Model attribute (_user): [%s]", u));
		return u;
	}
	
	
	@ModelAttribute(value="_isUser")
	protected boolean isUser(@AuthenticationPrincipal User u) {
		return hasAuthority(u, "MONEY_USER");
	}
	
	@ModelAttribute(value="_isAdmin")
	protected boolean isAdmin(@AuthenticationPrincipal User u) {
		return hasAuthority(u, "MONEY_ADMIN");
	}
	
	private boolean hasAuthority(User u, String name) {
		if (u != null) {
			for (GrantedAuthority auth : u.getAuthorities()) {
				if (auth.getAuthority().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@ModelAttribute(value="_ctxPath")
	protected String getWebContextPath(HttpServletRequest req) {
		return req.getContextPath();
	}
	
}
