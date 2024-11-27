package com.slepeweb.site.control;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.StringWrapper;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.service.LoglevelUpdateService;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.service.ComponentService;
import com.slepeweb.site.service.NavigationService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BaseController {
	public static Logger LOG = Logger.getLogger(BaseController.class);
	public static final String USER = "_user";
	public static final String ITEM = "_item";
	public static final String SITE = "_site";
	public static final String SHORT_SITENAME = "_shortSitename";
	
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
	
	/*
	 * NOTEZ BIEN!
	 * 
	 * ALL requests hit the Spring dispatcher servlet first, and MOST of these will be
	 * forwarded to the CmsDeliveryServlet. It's here in the CmsDeliveryServlet where request
	 * attributes ITEM, USER, etc are created.
	 * 
	 * So, if CmsDeliveryServlet forwards back again to a Spring controller, that controller
	 * will have access to model attributes ITEM, USER, etc. Otherwise, they will have null values.
	 */
	
	
	@ModelAttribute(value=ITEM)
	public Item getRequestItem(HttpServletRequest req) {
		Item i = (Item) req.getAttribute(ITEM);
		//LOG.info(String.format("Model attribute retrieved by BaseController (%s): [%s]", ITEM, i));
		return i;
	}
	
	@ModelAttribute(value=SITE)
	public Site getRequestSite(HttpServletRequest req) {
		Site s = (Site) req.getAttribute(SITE);
		LOG.trace(String.format("Model attribute (%s): [%s]", SITE, s));
		return s;
	}
	
	@ModelAttribute(value=SHORT_SITENAME)
	protected String getShortSitename(HttpServletRequest req) {
		Site s = (Site) req.getAttribute(SITE);
		String shortName = s != null ? s.getShortname() : "";
		LOG.trace(String.format("Model attribute (%s): [%s]", SHORT_SITENAME, shortName));
		return shortName;
	}
	
	@ModelAttribute(value=USER)
	protected User getUser(HttpServletRequest req) {
		User u = (User) req.getSession().getAttribute(USER);
		LOG.trace(String.format("Model attribute (%s): [%s]", USER, u));
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
		
		List<Link> components = i.getComponents();
		p.setComponents(this.componentService.getComponents(components, LinkName.std));
		p.getLeftSidebar().setComponents(this.componentService.getComponents(components, LinkName.LEFT_SIDE));
		p.getRightSidebar().setComponents(this.componentService.getComponents(components, LinkName.RIGHT_SIDE));
		
		model.addAttribute("_page", p);
		return p;
	}
	
	/*
	 * I can't see a way to make the server process form data as utf-8. All attempts to set the
	 * character encoding for dealing with non-english search terms as utf-8 have failed.
	 */
	protected String iso2utf8(String s) {
		if (StringUtils.isNotBlank(s)) {
			try {
				return new String(s.getBytes("ISO-8859-1"));
			}
			catch (Exception e) {
			}
		}
		return s;
	}
}
