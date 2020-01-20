package com.slepeweb.cms.control;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.component.ServerConfig;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.service.ItemTypeService;
import com.slepeweb.cms.service.LoglevelUpdateService;

@Controller
public class BaseController {
	
	@Autowired protected ServerConfig config;
	@Autowired private LoglevelUpdateService loglevelUpdateService;
	@Autowired private ItemTypeService itemTypeService;
	
	private String contextPath;

	@ModelAttribute(value="applicationContextPath")
	public String getApplicationContextPath(HttpSession session) {
		if (this.contextPath == null) {
			this.contextPath = session.getServletContext().getContextPath();
		}
		return this.contextPath;
	}
	
	@ModelAttribute(value="_serverConfig")
	public ServerConfig getConfig() {
		return this.config;
	}
	
	@ModelAttribute(value="_user")
	protected User getUser(@AuthenticationPrincipal User u) {
		return u;
	}
	
	@ModelAttribute(value="_isAuthor")
	protected boolean isAdmin(@AuthenticationPrincipal User u) {
		return hasAuthority(u, "CMS_ADMIN");
	}
	
	@ModelAttribute(value="_loglevel")
	protected boolean getLogLevelTrigger(@RequestParam(value="loglevel", required=false) String trigger) {
		if (trigger != null) {
			this.loglevelUpdateService.updateLoglevels();
			return true;
		}
		
		return false;
	}
	
	@ModelAttribute(value="_productTypeId")
	protected String getProductTypeId() {
		ItemType productType = this.itemTypeService.getItemType(ItemTypeName.PRODUCT);
		if (productType != null) {
			return String.valueOf(productType.getId());
		}
		return "0";
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
