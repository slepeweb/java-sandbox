package com.slepeweb.sandbox.www.control;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.slepeweb.sandbox.mongo.UserDAO;
import com.slepeweb.sandbox.www.model.LoginForm;
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
		Page page = new Page().
			setPath("/projects").
			setView("projects").
			setTitle("Projects").
			addRole(Role.ADMIN).addRole(Role.AGENT);
		
		if (page.isAccessibleBy(getUser(session))) {
			ModelAndView modelAndView = new ModelAndView(page.getView());			
			page.getHeader().getStylesheets().add("/resources/css/slepeweb.css");
			modelAndView.addObject("_page", page);			
			page.getHeader().setTopNavigation(this.navigationService.getTopNavigation(page));
			return modelAndView;
		}
		else {
			return doLoginForm(page.getPath());
		}
	}
	
	private User getUser(HttpSession session) {
		return (User) session.getAttribute("_user");
	}
	
	
	@RequestMapping(value = "/loginForm")
	public ModelAndView doLoginForm(String nextPath) {
		LoginPage page = new LoginPage();
		page.setPath("/loginForm").setTitle("Login");
		page.setNextPath(nextPath);
		
		LoginForm form = new LoginForm();
		form.setNextPath(nextPath);
		
		ModelAndView modelAndView = new ModelAndView("loginForm", "loginForm", form);			
		page.getHeader().getStylesheets().add("/resources/css/slepeweb.css");
		modelAndView.addObject("_page", page);			
		page.getHeader().setTopNavigation(this.navigationService.getTopNavigation(page));
		return modelAndView;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String doLogon(@ModelAttribute LoginForm loginForm, HttpServletRequest req, HttpServletResponse resp) {
		// Find this user in the db
		User target = this.userDAOservice.findUser(loginForm.getAlias());
		String nextPath = "/loginForm";
		
		if (target != null) {
			// Check passwords match
			BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
			if (passwordEncryptor.checkPassword(loginForm.getPassword(), target.getEncryptedPassword())) {
				// Success
				req.getSession().setAttribute("_user", target);
				nextPath = loginForm.getNextPath();
			}			
		}
		
		return "redirect:" + nextPath;
	}
}
