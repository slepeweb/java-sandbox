package com.slepeweb.site.control;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.component.Config;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.site.model.LinkTarget;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.service.ComponentService;

@Controller
public class BaseController {
	
	@Autowired protected Config config;
	@Autowired private ComponentService componentService;
	@Autowired private CmsService cmsService;

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
	
	protected Page standardTemplate(Item i) {	
		
		Page page = new Page().
				setTitle(i.getFieldValue("title")).
				setBody(i.getFieldValue("bodytext", "")).
				setTopNavigation(getTopNavigation(i));
		
		page.setHeading(page.getTitle());
		page.setComponents(this.componentService.getComponents(i.getComponents(), "main"));
		page.getHeader().setBreadcrumbs(i);
		
		return page;
	}

	protected List<LinkTarget> getTopNavigation(Item i) {
		List<LinkTarget> nav = new ArrayList<LinkTarget>();
		Item root = this.cmsService.getItemService().getItem(i.getSite().getId(), "/");
		LinkTarget lt;
		
		if (root != null) {
			for (Link l : root.getBindings()) {
				lt = new LinkTarget(l.getChild()).
						setSelected(i.getPath().startsWith(l.getChild().getPath()));
				nav.add(lt);
			}
		}
		return nav;
	}

}
