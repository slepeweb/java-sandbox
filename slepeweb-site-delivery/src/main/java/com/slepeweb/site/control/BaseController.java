package com.slepeweb.site.control;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.StringWrapper;
import com.slepeweb.cms.service.LoglevelUpdateService;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.service.ComponentService;
import com.slepeweb.site.service.NavigationService;

@Controller
public class BaseController {
	public static Logger LOG = Logger.getLogger(BaseController.class);
	public static final String USER = "_userOld";
	public static final String ITEM = "_item";
	public static final String SITE = "_site";
	
	@Autowired private ComponentService componentService;
	@Autowired private LoglevelUpdateService loglevelUpdateService;
	@Autowired private NavigationService navigationService;	

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
		LOG.info(String.format("Model attribute retrieved by BaseController (%s): [%s]", ITEM, i));
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
	
	@ModelAttribute(value="_user")
	protected com.slepeweb.cms.bean.User getUser(HttpServletRequest req) {
		com.slepeweb.cms.bean.User u = (com.slepeweb.cms.bean.User) req.getSession().getAttribute("_user");
		LOG.trace(String.format("Model attribute (_user): [%s]", u));
		return u;
	}
	
	
	protected String composeJspPath(String shortHostName, String viewNameSuffix) {
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
				setView(composeJspPath(shortSitename, viewNameSuffix));
		
		p.setComponents(this.componentService.getComponents(i.getComponents(), LinkName.MAIN));
		p.getLeftSidebar().setComponents(this.componentService.getComponents(i.getComponents(), LinkName.LEFT_SIDE));
		p.getRightSidebar().setComponents(this.componentService.getComponents(i.getComponents(), LinkName.RIGHT_SIDE));
		
		model.addAttribute("_page", p);
		return p;
	}
}
