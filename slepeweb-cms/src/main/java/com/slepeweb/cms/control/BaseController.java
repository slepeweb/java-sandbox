package com.slepeweb.cms.control;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class BaseController {
	
	private String contextPath;

	@ModelAttribute(value="applicationContextPath")
	public String getApplicationContextPath(HttpSession session) {
		if (this.contextPath == null) {
			this.contextPath = session.getServletContext().getContextPath();
		}
		return this.contextPath;
	}
	
}
