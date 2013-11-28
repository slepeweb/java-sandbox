package com.slepeweb.sandbox.www.control;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.slepeweb.sandbox.orm.Role;
import com.slepeweb.sandbox.orm.User;
import com.slepeweb.sandbox.www.model.Link;
import com.slepeweb.sandbox.www.model.LoginForm;
import com.slepeweb.sandbox.www.model.LoginPage;
import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.service.NavigationService;

@Controller
public class BaseController {

	@Autowired
	private NavigationService navigationService;
	
	@ModelAttribute("_user")
	public User getUser(HttpSession session) {
		return (User) session.getAttribute("_user");
	}
	
	@ModelAttribute(value="userHasAgentRole")
	public boolean userHasAgentRole(HttpSession session) {
		User user = getUser(session);
		if (user != null) {
			return user.hasRole(Role.AGENT_ROLE);
		}
		return false;
	}
	
	@ModelAttribute(value="userHasAdminRole")
	public boolean userHasAdminRole(HttpSession session) {
		User user = getUser(session);
		if (user != null) {
			return user.hasRole(Role.GLOBAL_ADMIN_ROLE);
		}
		return false;
	}
	
	protected List<Link> getTopNavigation(Page page, User user) {
		return this.navigationService.getTopNavigation(page, user);
	}
	
	protected String checkAccessibility(Page requestedPage, User user, ModelMap map) {
		if (requestedPage.isAccessibleBy(user)) {
			map.addAttribute("_page", requestedPage);			
			return requestedPage.getView();
		}
		else {
			return doLoginForm(user, requestedPage.getHref(), map);
		}
	}	
	
	protected String doLoginForm(User user, String nextPath, ModelMap map) {
		Page page = getLoginPage(user, nextPath);
		
		LoginForm form = new LoginForm();
		form.setNextPath(nextPath);
		
		map.addAttribute(form);
		map.addAttribute("_page", page);			
		return page.getView();
	}
	
	protected Page getLoginPage(User user, String nextPath) {
		LoginPage page = new LoginPage();
		page.setNextView(nextPath);
		page.
			setHref("/login").
			setView("login").
			setTitle("Login").
			addStylesheet("/resources/css/slepeweb.css");
		
		return page.setTopNavigation(getTopNavigation(page, user));
	}	
	
	protected void removeModelAttributes(ModelMap map) {
		// Eliminate existing model attributes, otherwise they get added by Spring
		// to the redirect URL
		map.remove("userHasAgentRole");
		map.remove("userHasAdminRole");
	}
}
