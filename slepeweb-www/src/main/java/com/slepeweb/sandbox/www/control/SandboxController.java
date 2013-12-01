package com.slepeweb.sandbox.www.control;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.service.NavigationService;

@Controller
@RequestMapping(value="/sandbox")
public class SandboxController extends BaseController {
	
	@Autowired
	private NavigationService navigationService;

	@RequestMapping(value="")
	public String forwardToPlatform(HttpSession session, ModelMap model) {
		return doPlatform(session, model);
	}
	
	@RequestMapping(value="platform")
	public String doPlatform(HttpSession session, ModelMap model) {
		Page page = new Page().
			setHref("/sandbox/platform").
			setTitle("Sandbox platform").
			setView("sandbox.platform").
			addStylesheet("/resources/css/slepeweb.css").
			addJavascript("/resources/js/sandbox.js");
		
		page.setTopNavigation(getTopNavigation(page, getLoggedInUser(session)));
		model.addAttribute("_page", page);		
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));		
		return page.getView();
	}
	
	@RequestMapping(value="ws")
	public String doServices(HttpSession session, ModelMap model) {
		Page page = new Page().
			setHref("/sandbox/ws").
			setTitle("Sandbox web services").
			setView("sandbox.ws").
			addStylesheet("/resources/css/slepeweb.css").
			addJavascript("/resources/js/sandbox.js");
		
		page.setTopNavigation(getTopNavigation(page, getLoggedInUser(session)));
		model.addAttribute("_page", page);			
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));
		return page.getView();
	}
	
}
