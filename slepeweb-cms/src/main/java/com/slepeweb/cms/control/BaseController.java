package com.slepeweb.cms.control;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.slepeweb.cms.component.Config;

@Controller
public class BaseController {
	
	@Autowired protected Config config;
	private String contextPath;

	@ModelAttribute(value="applicationContextPath")
	public String getApplicationContextPath(HttpSession session) {
		if (this.contextPath == null) {
			this.contextPath = session.getServletContext().getContextPath();
		}
		return this.contextPath;
	}
	
	@ModelAttribute(value="config")
	public Config getConfig() {
		this.config.setLiveDelivery(false);
		return this.config;
	}
	
	@ModelAttribute(value="_user")
	protected User getUser() {
		Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (obj instanceof User) {
			return (User) obj;
		}
		return null;
	}
	
	@ModelAttribute(value="_isAuthor")
	protected boolean isAdmin(@ModelAttribute(value="_user") User u) {
		return hasAuthority(u, "CMS_ADMIN");
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
}
