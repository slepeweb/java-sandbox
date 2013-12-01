package com.slepeweb.sandbox.www.control;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.sandbox.mongo.ConfigDAO;
import com.slepeweb.sandbox.orm.User;
import com.slepeweb.sandbox.orm.UserDao;
import com.slepeweb.sandbox.www.bean.SessionAttr;
import com.slepeweb.sandbox.www.model.LoginForm;
import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.service.RomeService;

@Controller
public class MainController extends BaseController {
	private static Logger LOG = Logger.getLogger(MainController.class);

	@Autowired
	private RomeService romeService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ConfigDAO configDAOservice;
	
	@RequestMapping(value="/about")
	public String doGeneric(HttpSession session, ModelMap model) {
		Page page = new Page().
			setHref("/about").
			setTitle("About").
			setView("home").
			addStylesheet("/resources/css/slepeweb.css");
		
		page.setTopNavigation(getTopNavigation(page, getLoggedInUser(session)));
		
		model.addAttribute("_page", page);
		model.addAttribute("_rss", this.romeService.getFeed("http://feeds.bbci.co.uk/news/technology/rss.xml"));
		return page.getView();
	}
	
	@RequestMapping(value = "/profile")
	public String doProjects(HttpSession session, ModelMap model) {
		Page page = new Page().
			setHref("/profile").
			setTitle("Profile").
			setView("projects").
//			addRole(Role.ADMIN).
//			addRole(Role.AGENT).
			addStylesheet("/resources/css/slepeweb.css");
		
		User loggedInUser = getLoggedInUser(session);
		page.setTopNavigation(getTopNavigation(page, loggedInUser));
		return checkAccessibility(page, loggedInUser, model);
	}
	
	@RequestMapping(value = "/contact")
	public String doContact(HttpSession session, ModelMap model) {
		Page page = new Page().
			setHref("/contact").
			setTitle("Contact us").
			setView("contact").
			addStylesheet("/resources/css/slepeweb.css");
		
		User loggedInUser = getLoggedInUser(session);
		page.setTopNavigation(getTopNavigation(page, loggedInUser));

		model.addAttribute("_page", page);
		return page.getView();
	}
		
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String doLoginForm(HttpSession session, @RequestParam String nextPath, ModelMap map) {
		return doLoginForm(getLoggedInUser(session), nextPath, map);
	}
		
	/*
	 * BEWARE: The objects in @ModelAttribute-annotated method arguments are automatically
	 * updated by Spring with the values of request parameters, if the parameter name
	 * matches an object property. This is exactly what you want for form command objects,
	 * but otherwise could have some serious side effects.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String doLogon(HttpSession session, @ModelAttribute LoginForm loginForm, BindingResult result, 
			ModelMap model) {
		
		boolean isError = false;
		
		if (! result.hasErrors()) {			
			// Find this user in the db
			User target = this.userDao.getUser(loginForm.getAlias());
			
			if (target == null) {
				// No such user
				isError = true;
			}
			else {
				// Found user - check passwords match
				BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
				if (passwordEncryptor.checkPassword(loginForm.getPassword(), target.getEncryptedPassword())) {
					// Password matches DB value
					session.setAttribute(SessionAttr.LOGGED_IN_USER, target);
					LOG.info(String.format("User logged in [%s]", target));
					
					removeModelAttributes(model);
					
					// Which host to redirect to?
					String host = this.configDAOservice.findValue("server.host.name", "www.slepeweb.com");
					return "redirect://" + host + loginForm.getNextPath();
				}
				else {
					// Password failure
					isError = true;
				}
			}
		}
		
		// Treat all errors the same
		if (isError) {
			/* 
			 * NB. If you don't specify the rejected value in the FieldError,
			 * then the bad field value is not displayed when the form is re-rendered
			 */
			String[] empty = new String[] {};
			result.addError(new FieldError("loginForm", "alias", loginForm.getAlias(), false, 
					empty, empty,
					"Username/password is not recognised - please try again"));
		}
		
		// Go back to login page
		model.addAttribute("_page", getLoginPage(null, loginForm.getNextPath()));			
		return "login";
	}
}
