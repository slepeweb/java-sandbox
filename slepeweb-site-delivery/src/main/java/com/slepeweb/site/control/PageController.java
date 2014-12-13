package com.slepeweb.site.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.servlet.CmsDeliveryServlet;

@Controller
public class PageController extends BaseController {
	
	@Autowired private CmsDeliveryServlet cmsDeliveryServlet;
	
	@RequestMapping(value="/**")	
	public void mainController(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {		
		this.cmsDeliveryServlet.doGet(req, res, model);
	}
	
	@RequestMapping(value="/spring/homepage")	
	public String homepage(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		// This view forwards to '/about'
		// Doing the work in the JSP, which is site dependant, whereas this controller
		// might be used in multiple sites.
		return getFullyQualifiedViewName(shortSitename, "homepage");
	}

	@RequestMapping(value="/spring/article/rightsidebar")	
	public String standardTemplateRightSidebar(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "article-011", model);
		return page.getView();
	}
	
	@RequestMapping(value="/spring/article/3col")	
	public String standardTemplate3Col(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "article-111", model);
		return page.getView();
	}
	
	@RequestMapping(value="/spring/article/leftnav")	
	public String standardTemplateLeftNav(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "article-leftnav", model);
		page.setLeftNavigation();
		return page.getView();
	}
		
	@RequestMapping(value="/spring/projects")
	public String projects(
		@ModelAttribute("_item") Item i, 
		@ModelAttribute("_shortSitename") String shortSitename, 
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout,
		ModelMap model) {
 
		Page page = getStandardPage(i, shortSitename, "article-010", model);
		return page.getView();
	}

	@RequestMapping(value="/spring/wsdemo")
	public String wsdemo(
		@ModelAttribute("_item") Item i, 
		@ModelAttribute("_shortSitename") String shortSitename, 
		ModelMap model) {
 
		Page page = getStandardPage(i, shortSitename, "article-leftnav", model);
		page.addJavascript("/resources/sws/js/sandbox.js");
		return page.getView();
 
	}

	@RequestMapping(value="/spring/login")
	public String login(
		@ModelAttribute("_item") Item i, 
		@ModelAttribute("_shortSitename") String shortSitename, 
		@RequestParam(value="error", required = false) String error,
		@RequestParam(value="logout", required = false) String logout,
		ModelMap model) {
 
		Page page = getStandardPage(i, shortSitename, "login", model);

		if (error != null) {
			model.addAttribute("error", "Invalid username and password!");
		}
 
		if (logout != null) {
			model.addAttribute("msg", "You've been successfully logged out.");
		}
 
		return page.getView(); 
	}

	@RequestMapping(value="/spring/spizza")	
	public String spizza(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "dummy", model);
		page.setLeftNavigation();
		req.setAttribute("_page", page);
		
		// Now hand-off the request to spring webflow
		return "forward:/webflow/spizza";
	}
	
}
