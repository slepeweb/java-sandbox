package com.slepeweb.site.control;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.component.Config;

@Controller
public class BaseController {
	
	@Autowired protected Config config;

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
	protected Principal getUser(HttpServletRequest req) {
		return req.getUserPrincipal();
	}
	
	protected String getFullyQualifiedViewName(String shortHostName, String viewNameSuffix) {
		return shortHostName + "/template/" + viewNameSuffix;
	}
}
