package com.slepeweb.sandbox.www.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.service.NavigationService;
import com.slepeweb.sandbox.www.service.RomeService;

@Controller
public class HomepageController {
	
	@Autowired
	private NavigationService navigationService;
	
	@Autowired
	private RomeService romeService;
	
	@RequestMapping(value = { "/home", "/homepage" })
	public ModelAndView doGeneric() {
		ModelAndView modelAndView = new ModelAndView("home");
		Page page = new Page().setPath("/home").setTitle("Home");
		modelAndView.addObject("_page", page);
		modelAndView.addObject("_rss", this.romeService.getFeed("http://feeds.bbci.co.uk/news/technology/rss.xml"));
		
		page.getHeader().setTopNavigation(this.navigationService.getTopNavigation(page));
		return modelAndView;
	}
	
	@RequestMapping(value = "/sandbox")
	public ModelAndView doSandbox() {
		ModelAndView modelAndView = new ModelAndView("sandbox");
		Page page = new Page().setPath("/sandbox").setTitle("Sandbox");
		modelAndView.addObject("_page", page);
		
		page.getHeader().setTopNavigation(this.navigationService.getTopNavigation(page));
		return modelAndView;
	}
	
	@RequestMapping(value = "/projects")
	public ModelAndView doProjects() {
		ModelAndView modelAndView = new ModelAndView("projects");
		Page page = new Page().setPath("/projects").setTitle("Projects");
		page.getHeader().getStylesheets().add("/resources/css/slepeweb.css");
		modelAndView.addObject("_page", page);
		
		page.getHeader().setTopNavigation(this.navigationService.getTopNavigation(page));
		return modelAndView;
	}
}
