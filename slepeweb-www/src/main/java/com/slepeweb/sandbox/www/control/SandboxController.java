package com.slepeweb.sandbox.www.control;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.service.NavigationService;
import com.slepeweb.sandbox.www.service.PageService;

@Controller
@RequestMapping(value="/sandbox")
public class SandboxController extends BaseController {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private NavigationService navigationService;

	@RequestMapping(value="")
	public String forwardToPlatform(HttpSession session, ModelMap model) {
		return doPlatform(session, model);
	}
	
	@RequestMapping(value="platform")
	public String doPlatform(HttpSession session, ModelMap model) {
		Page page = this.pageService.getPage(PageService.SANDBOX_PLATFORM);				
		page.setTopNavigation(getTopNavigation(page, getLoggedInUser(session)));
		model.addAttribute("_page", page);		
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));		
		return page.getView();
	}
	
	@RequestMapping(value="ws")
	public String doServices(HttpSession session, ModelMap model) {
		Page page = this.pageService.getPage(PageService.SANDBOX_WS);						
		page.setTopNavigation(getTopNavigation(page, getLoggedInUser(session)));
		model.addAttribute("_page", page);			
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));
		return page.getView();
	}
	
}
