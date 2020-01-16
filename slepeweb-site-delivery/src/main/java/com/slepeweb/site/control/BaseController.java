package com.slepeweb.site.control;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.StringWrapper;
import com.slepeweb.cms.component.ServerConfig;
import com.slepeweb.cms.service.LoglevelUpdateService;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.service.ComponentService;
import com.slepeweb.site.service.NavigationService;

@Controller
public class BaseController {
	private static Logger LOG = Logger.getLogger(BaseController.class);
	public static final String USER = "_user";
	public static final String ITEM = "_item";
	public static final String SITE = "_site";
	
	@Autowired protected ServerConfig config;
	@Autowired private ComponentService componentService;
	@Autowired private LoglevelUpdateService loglevelUpdateService;
	@Autowired private NavigationService navigationService;	

	@ModelAttribute(value="_serverConfig")
	public ServerConfig getConfig() {
		return this.config;
	}
	
	@ModelAttribute(value="_loglevel")
	protected boolean getLogLevelTrigger(@RequestParam(value="loglevel", required=false) String trigger) {
		if (trigger != null) {
			this.loglevelUpdateService.updateLoglevels();
			LOG.info("Updated logging levels");
			return true;
		}
		
		return false;
	}
	
	@ModelAttribute(value=ITEM)
	public Item getRequestItem(HttpServletRequest req) {
		Item i = (Item) req.getAttribute(ITEM);
		LOG.trace(String.format("Model attribute (%s): [%s]", ITEM, i));
		return i;
	}
	
	@ModelAttribute(value=SITE)
	public Site getRequestSite(HttpServletRequest req) {
		Site s = (Site) req.getAttribute(SITE);
		LOG.trace(String.format("Model attribute (%s): [%s]", SITE, s));
		return s;
	}
	
	@ModelAttribute(value="_shortSitename")
	protected String getShortSitename(HttpServletRequest req) {
		Site s = (Site) req.getAttribute(SITE);
		String shortName = s != null ? s.getShortname() : "";
		LOG.trace(String.format("Model attribute (_shortSitename): [%s]", shortName));
		return shortName;
	}
	
	@ModelAttribute(value=USER)
	protected User getUser(@AuthenticationPrincipal User u) {
		LOG.trace(String.format("Model attribute (_user): [%s]", u));
		return u;
	}
	
	
	@ModelAttribute(value="_isGuest")
	protected boolean isGuest(@AuthenticationPrincipal User u) {
		return hasAuthority(u, "SWS_GUEST");
	}
	
	@ModelAttribute(value="_isAdmin")
	protected boolean isAdmin(@AuthenticationPrincipal User u) {
		return hasAuthority(u, "SWS_ADMIN");
	}
	
	@ModelAttribute(value="_isPasswordClient")
	protected boolean isPasswordClient(@AuthenticationPrincipal User u) {
		return hasAuthority(u, "SWS_PWD");
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
		String view = shortHostName + "/template/" + viewNameSuffix;
		LOG.trace(String.format("CMS page view: [%s]", view));
		return view;
	}
	
	protected Page getStandardPage(Item i, String shortSitename, String viewNameSuffix, ModelMap model) {			
		Page p = new Page(this.navigationService).
				setTitle(i.getFieldValue("title")).
				setHeading(i.getFieldValue("title")).
				setBody(i.getFieldValueResolved("bodytext", new StringWrapper(""))).
				setItem(i).
				setView(getFullyQualifiedViewName(shortSitename, viewNameSuffix));
		
		p.setComponents(this.componentService.getComponents(i.getComponents(), LinkName.MAIN));
		p.getLeftSidebar().setComponents(this.componentService.getComponents(i.getComponents(), LinkName.LEFT_SIDE));
		p.getRightSidebar().setComponents(this.componentService.getComponents(i.getComponents(), LinkName.RIGHT_SIDE));
		
		model.addAttribute("_page", p);
		return p;
	}
}
