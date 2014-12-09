package com.slepeweb.site.control;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.component.Config;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.service.ComponentService;

@Controller
public class BaseController {
	
	@Autowired protected Config config;
	@Autowired private ComponentService componentService;

	@ModelAttribute(value="config")
	public Config getConfig() {
		this.config.setLiveDelivery(false);
		return this.config;
	}
	
	@ModelAttribute(value="_item")
	public Item getRequestItem(HttpServletRequest req) {
		return (Item) req.getAttribute("_item");
	}
	
	@ModelAttribute(value="_site")
	public Site getRequestSite(HttpServletRequest req) {
		Item i = (Item) req.getAttribute("_item");
		if (i != null) {
			return i.getSite();
		}
		return null;
	}
	
	@ModelAttribute(value="_shortHostname")
	protected String getShortHostName(@ModelAttribute("_item") Item i) {
		return i != null && i.getSite() != null ? i.getSite().getShortname() : "";
	}
	
	@ModelAttribute(value="_user")
	protected User getUser() {
		Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (obj instanceof User) {
			return (User) obj;
		}
		return null;
	}
	
	
	@ModelAttribute(value="_isGuest")
	protected boolean isGuest(@ModelAttribute(value="_user") User u) {
		return hasAuthority(u, "SWS_GUEST");
	}
	
	@ModelAttribute(value="_isAdmin")
	protected boolean isAdmin(@ModelAttribute(value="_user") User u) {
		return hasAuthority(u, "SWS_ADMIN");
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
	
	protected String getFullyQualifiedViewName(String shortHostName, String viewNameSuffix) {
		return shortHostName + "/template/" + viewNameSuffix;
	}
	
	protected Page standardTemplate(Item i, @ModelAttribute("_user") User u) {	
		
		Page page = new Page().
				setTitle(i.getFieldValue("title")).
				setBody(i.getFieldValue("bodytext", "")).
				setItem(i).
				setUser(u);
		
		page.setHeading(page.getTitle());
		page.setComponents(this.componentService.getComponents(i.getComponents(), "main"));
		
		return page;
	}

}
