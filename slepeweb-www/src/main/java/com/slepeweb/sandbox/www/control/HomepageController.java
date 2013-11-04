package com.slepeweb.sandbox.www.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.slepeweb.sandbox.mongo.UserDAO;
import com.slepeweb.sandbox.www.model.Link;
import com.slepeweb.sandbox.www.model.LoginForm;
import com.slepeweb.sandbox.www.model.LoginPage;
import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.model.User;
import com.slepeweb.sandbox.www.model.User.Role;
import com.slepeweb.sandbox.www.service.NavigationService;
import com.slepeweb.sandbox.www.service.RomeService;

@Controller
public class HomepageController {

	@Autowired
	private NavigationService navigationService;
	
	@Autowired
	private RomeService romeService;
	
	@Autowired
	private UserDAO userDAOservice;
	
	@RequestMapping(value = { "/home", "/homepage" })
	public ModelAndView doGeneric() {
		Page page = new Page().
			setHref("/home").
			setTitle("Home").
			setView("home").
			setTopNavigation(getTopNavigation());
		
		ModelAndView modelAndView = new ModelAndView(page.getView());
		modelAndView.addObject("_page", page);
		modelAndView.addObject("_rss", this.romeService.getFeed("http://feeds.bbci.co.uk/news/technology/rss.xml"));
		return modelAndView;
	}
	
	@RequestMapping(value = "/sandbox")
	public ModelAndView doSandbox(HttpSession session) {
		Page page = new Page().
			setHref("/sandbox").
			setTitle("Sandbox").
			setView("sandbox").
			addRole(Role.ADMIN).
			setTopNavigation(getTopNavigation());
		
		return checkAccessibility(page, getUser(session));
	}
	
	@RequestMapping(value = "/projects")
	public ModelAndView doProjects(HttpSession session) {
		Page page = new Page().
			setHref("/projects").
			setTitle("Projects").
			setView("projects").
			addRole(Role.AGENT).
			setTopNavigation(getTopNavigation()).
			addStylesheet("/resources/css/slepeweb.css");
		
		return checkAccessibility(page, getUser(session));
	}
	
	private User getUser(HttpSession session) {
		return (User) session.getAttribute("_user");
	}
	
	private ModelAndView checkAccessibility(Page requestedPage, User user) {
		if (requestedPage.isAccessibleBy(user)) {
			ModelAndView modelAndView = new ModelAndView(requestedPage.getView());			
			modelAndView.addObject("_page", requestedPage);			
			return modelAndView;
		}
		else {
			return doLoginForm(requestedPage.getView());
		}
	}	
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView doLoginForm(String nextPath) {
		Page page = getLoginPage(nextPath);
		
		LoginForm form = new LoginForm();
		form.setNextPath(nextPath);
		
		ModelAndView modelAndView = new ModelAndView(page.getView());
		modelAndView.addObject(form);
		modelAndView.addObject("_page", page);			
		return modelAndView;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String doLogon(@ModelAttribute LoginForm loginForm, BindingResult result, Model model,
			HttpServletRequest req, HttpServletResponse resp) {
		
		boolean isError = false;
		
		if (! result.hasErrors()) {			
			// Find this user in the db
			User target = this.userDAOservice.findUser(loginForm.getAlias());
			
			if (target == null) {
				// No such user
				isError = true;
			}
			else {
				// Found user - check passwords match
				BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
				if (passwordEncryptor.checkPassword(loginForm.getPassword(), target.getEncryptedPassword())) {
					// Password matches DB value
					req.getSession().setAttribute("_user", target);
					return "redirect:" + loginForm.getNextPath();
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
		model.addAttribute("_page", getLoginPage(loginForm.getNextPath()));			
		return "login";
	}
	
	private Page getLoginPage(String nextPath) {
		LoginPage page = new LoginPage();
		page.setNextView(nextPath);
		page.
			setHref("/login").
			setView("login").
			setTitle("Login").
			setTopNavigation(getTopNavigation()).
			addStylesheet("/resources/css/slepeweb.css");
		
		return page;
	}
	
	private List<Link> getTopNavigation() {
		return this.navigationService.getTopNavigation();
	}
}
