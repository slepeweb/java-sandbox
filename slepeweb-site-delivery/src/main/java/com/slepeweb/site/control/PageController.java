package com.slepeweb.site.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.site.model.LinkTarget;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.model.Sidebar;
import com.slepeweb.site.service.ComponentService;
import com.slepeweb.site.servlet.CmsDeliveryServlet;

@Controller
public class PageController extends BaseController {
	
	@Autowired private CmsService cmsService;
	@Autowired private CmsDeliveryServlet cmsDeliveryServlet;
	@Autowired private ComponentService componentService;
	
	@RequestMapping(value="/**")	
	public void mainController(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {		
		this.cmsDeliveryServlet.doGet(req, res, model);
	}
	
	@RequestMapping(value="/spring/homepage")	
	public String homepage(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortHostname") String shortHostname, 
			ModelMap model) {	
		
		standardTemplate(i, shortHostname, model);
		return getViewName(shortHostname, "home");
	}

	@RequestMapping(value="/spring/article")	
	public String standardTemplate(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortHostname") String shortHostname, 
			ModelMap model) {	
		
		Page page = new Page().
				setTitle(i.getFieldValue("title")).
				setBody(i.getFieldValue("bodytext", "")).
				setTopNavigation(getTopNavigation(i));
		
		page.setHeading(page.getTitle());
		//page.getHeader().getStylesheets().add("/resources/sws/css/slepeweb.css");
		Sidebar rightSidebar = new Sidebar();
		page.setRightSidebar(rightSidebar);
		rightSidebar.setComponents(this.componentService.getComponents(i.getComponents(), "rightside"));
		page.setComponents(this.componentService.getComponents(i.getComponents(), "main"));
		
		model.addAttribute("_page", page);
		return getViewName(shortHostname, "article");
	}

	@RequestMapping(value="/spring/projects")
	public String projects(
		@ModelAttribute("_item") Item i, 
		@ModelAttribute("_shortHostname") String shortHostname, 
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout,
		ModelMap model) {
 
		standardTemplate(i, shortHostname, model);
		return getViewName(shortHostname, "wide-article");
 
	}

	@RequestMapping(value="/spring/login")
	public String login(
		@ModelAttribute("_item") Item i, 
		@ModelAttribute("_shortHostname") String shortHostname, 
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout,
		ModelMap model) {
 
		standardTemplate(i, shortHostname, model);

		if (error != null) {
			model.addAttribute("error", "Invalid username and password!");
		}
 
		if (logout != null) {
			model.addAttribute("msg", "You've been logged out successfully.");
		}
 
		return getViewName(shortHostname, "login"); 
	}

	private List<LinkTarget> getTopNavigation(Item requestItem) {
		List<LinkTarget> nav = new ArrayList<LinkTarget>();
		Item root = this.cmsService.getItemService().getItem(requestItem.getSite().getId(), "/");
		if (root != null) {
			for (Link l : root.getBindings()) {
				nav.add(new LinkTarget(l.getChild()));
			}
		}
		return nav;
	}
}
