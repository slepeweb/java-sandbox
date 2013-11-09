package com.slepeweb.sandbox.www.control;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@RequestMapping(value = "/about")
	public ModelAndView doGeneric(@ModelAttribute("_user") User user) {
		Page page = new Page().
			setHref("/about").
			setTitle("About").
			setView("home").
			addStylesheet("/resources/css/slepeweb.css");
		
		page.setTopNavigation(getTopNavigation(page, user));
		
		ModelAndView modelAndView = new ModelAndView(page.getView());
		modelAndView.addObject("_page", page);
		modelAndView.addObject("_rss", this.romeService.getFeed("http://feeds.bbci.co.uk/news/technology/rss.xml"));
		return modelAndView;
	}
	
	@RequestMapping(value = "/sandbox")
	public ModelAndView doSandbox(@ModelAttribute("_user") User user) {
		Page page = new Page().
			setHref("/sandbox").
			setTitle("Sandbox").
			setView("sandbox").
			addRole(Role.ADMIN).
			addStylesheet("/resources/css/slepeweb.css").
			addJavascript("/resources/js/sandbox.js");
		
		page.setTopNavigation(getTopNavigation(page, user));
		return checkAccessibility(page, user);
	}
	
	@RequestMapping(value = "/profile")
	public ModelAndView doProjects(@ModelAttribute("_user") User user) {
		Page page = new Page().
			setHref("/profile").
			setTitle("Profile").
			setView("projects").
			addStylesheet("/resources/css/slepeweb.css");
		
		page.setTopNavigation(getTopNavigation(page, user));

		ModelAndView modelAndView = new ModelAndView(page.getView());			
		modelAndView.addObject("_page", page);			
		return modelAndView;
	}
	
	@RequestMapping(value = "/contact")
	public ModelAndView doContact(@ModelAttribute("_user") User user) {
		Page page = new Page().
			setHref("/contact").
			setTitle("Contact us").
			setView("contact").
			addStylesheet("/resources/css/slepeweb.css");
		
		page.setTopNavigation(getTopNavigation(page, user));

		ModelAndView modelAndView = new ModelAndView(page.getView());
		modelAndView.addObject("_page", page);
		return modelAndView;
	}
	
	@ModelAttribute("_user")
	public User getUser(HttpSession session) {
		return (User) session.getAttribute("_user");
	}
	
	@ModelAttribute(value="userHasAgentRole")
	public boolean userHasAgentRole(@ModelAttribute("_user") User user) {
		if (user != null) {
			return user.hasRole(Role.AGENT);
		}
		return false;
	}
	
	@ModelAttribute(value="userHasAdminRole")
	public boolean userHasAdminRole(@ModelAttribute("_user") User user) {
		if (user != null) {
			return user.hasRole(Role.ADMIN);
		}
		return false;
	}
	
	private ModelAndView checkAccessibility(Page requestedPage, User user) {
		if (requestedPage.isAccessibleBy(user)) {
			ModelAndView modelAndView = new ModelAndView(requestedPage.getView());			
			modelAndView.addObject("_page", requestedPage);			
			return modelAndView;
		}
		else {
			return doLoginForm(user, requestedPage.getView());
		}
	}	
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView doLoginForm(@RequestParam String nextPath, @ModelAttribute("_user") User user) {
		return doLoginForm(user, nextPath);
	}
	
	private ModelAndView doLoginForm(User user, String nextPath) {
		Page page = getLoginPage(user, nextPath);
		
		LoginForm form = new LoginForm();
		form.setNextPath(nextPath);
		
		ModelAndView modelAndView = new ModelAndView(page.getView());
		modelAndView.addObject(form);
		modelAndView.addObject("_page", page);			
		return modelAndView;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String doLogon(@ModelAttribute LoginForm loginForm, BindingResult result, Model model,
			HttpSession session) {
		
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
					session.setAttribute("_user", target);
					
					// TODO: Note that this form of redirect adds request params
					// corresponding to existinf=g model attributes.
					// Eg. Where loginForm.getNextPath() equals "/about", the redirect is to:
					//   /about?userHasAgentRole=false&userHasAdminRole=false
					// TODO: How can you stop this?
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
		model.addAttribute("_page", getLoginPage(null, loginForm.getNextPath()));			
		return "login";
	}
	
	private Page getLoginPage(User user, String nextPath) {
		LoginPage page = new LoginPage();
		page.setNextView(nextPath);
		page.
			setHref("/login").
			setView("login").
			setTitle("Login").
			addStylesheet("/resources/css/slepeweb.css");
		
		return page.setTopNavigation(getTopNavigation(page, user));
	}
	
	private List<Link> getTopNavigation(Page page, User user) {
		return this.navigationService.getTopNavigation(page, user);
	}
}
