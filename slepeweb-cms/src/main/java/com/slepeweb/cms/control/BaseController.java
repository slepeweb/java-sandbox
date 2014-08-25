package com.slepeweb.cms.control;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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
	
}
