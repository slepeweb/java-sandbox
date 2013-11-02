package com.slepeweb.sandbox.www.control;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.slepeweb.sandbox.mongo.UserDAO;
import com.slepeweb.sandbox.www.model.LoginPage;
import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.model.User;
import com.slepeweb.sandbox.www.model.User.Role;
import com.slepeweb.sandbox.www.service.NavigationService;
import com.slepeweb.sandbox.www.service.RomeService;

@Controller
public class HomepageController {
	private static Logger LOG = Logger.getLogger(HomepageController.class);
	@Autowired
	private NavigationService navigationService;
	
	@Autowired
	private RomeService romeService;
	
	@Autowired
	private UserDAO userDAOservice;
	
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
	public ModelAndView doSandbox(HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("sandbox");
		Page page = new Page().setPath("/sandbox").setTitle("Sandbox").addRole(Role.ADMIN).addRole(Role.AGENT);
		modelAndView.addObject("_page", page);
		
		page.getHeader().setTopNavigation(this.navigationService.getTopNavigation(page));
		return modelAndView;
	}
	
	@RequestMapping(value = "/projects")
	public ModelAndView doProjects(HttpSession session) {
		User u = this.userDAOservice.findUser(1);
		if (u != null) {
			LOG.info(String.format("Found user [%s]", u.getName()));
		}
		
		Page page = new Page().setPath("/projects").setTitle("Projects").addRole(Role.ADMIN).addRole(Role.AGENT);
		
		if (page.isAccessibleBy(getUser(session))) {
			ModelAndView modelAndView = new ModelAndView("projects");			
			page.getHeader().getStylesheets().add("/resources/css/slepeweb.css");
			modelAndView.addObject("_page", page);			
			page.getHeader().setTopNavigation(this.navigationService.getTopNavigation(page));
			return modelAndView;
		}
		else {
			return doLogin(page.getPath());
		}
	}
	
	private User getUser(HttpSession session) {
		return (User) session.getAttribute("_user");
	}
	
	private ModelAndView doLogin(String targetPath) {
		LoginPage page = new LoginPage();
		page.setPath("/login").setTitle("Login");
		page.setNextPath(targetPath);
		
		ModelAndView modelAndView = new ModelAndView("login");			
		page.getHeader().getStylesheets().add("/resources/css/slepeweb.css");
		modelAndView.addObject("_page", page);			
		page.getHeader().setTopNavigation(this.navigationService.getTopNavigation(page));
		return modelAndView;
	}
}
