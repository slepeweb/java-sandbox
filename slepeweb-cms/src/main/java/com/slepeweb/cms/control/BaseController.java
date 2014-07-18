package com.slepeweb.cms.control;

import org.springframework.stereotype.Controller;

@Controller
public class BaseController {

//	@Autowired
//	private ConfigDao configDao;
//	
//	@ModelAttribute(value=SessionAttr.LOGGED_IN_USER)
//	protected User getLoggedInUser(HttpSession session) {
//		return (User) session.getAttribute(SessionAttr.LOGGED_IN_USER);
//	}
//	
//	@ModelAttribute(value="userHasAgentRole")
//	public boolean userHasAgentRole(HttpSession session) {
//		return checkPrivileges(session, Role.AGENT_ROLE);
//	}
//	
//	@ModelAttribute(value="userHasAdminRole")
//	public boolean userHasAdminRole(HttpSession session) {
//		return checkPrivileges(session, Role.GLOBAL_ADMIN_ROLE);
//	}
//	
//	@ModelAttribute(value="userHasUserAdminRole")
//	public boolean userHasUserAdminRole(HttpSession session) {
//		return checkPrivileges(session, Role.USER_ADMIN_ROLE);
//	}
//	
//	private boolean checkPrivileges(HttpSession session, String roleName) {
//		User user = getLoggedInUser(session);
//		return user != null && user.hasRole(roleName);
//	}
//	
//	protected List<Link> getTopNavigation(Page page, User loggedInUser) {
//		return this.navigationService.getTopNavigation(page, loggedInUser);
//	}
//	
//	protected String checkAccessibility(Page requestedPage, User loggedInUser, ModelMap map) {
//		if (requestedPage.isAccessibleBy(loggedInUser)) {
//			map.addAttribute("_page", requestedPage);			
//			return requestedPage.getView();
//		}
//		else {
//			return doLoginForm(loggedInUser, requestedPage.getHref(), map);
//		}
//	}	
//	
//	protected String doLoginForm(User loggedInUser, String nextPath, ModelMap map) {
//		Page page = getLoginPage(loggedInUser, nextPath);
//		
//		LoginForm form = new LoginForm();
//		form.setNextPath(nextPath);
//		
//		map.addAttribute(form);
//		map.addAttribute("_page", page);			
//		return page.getView();
//	}
//	
//	protected Page getLoginPage(User loggedInUser, String nextPath) {
//		LoginPage page = new LoginPage();
//		page.setNextView(nextPath);
//		page.
//			setHref("/login").
//			setView("login").
//			setTitle("Login").
//			addStylesheet("/resources/css/slepeweb.css");
//		
//		return page.setTopNavigation(getTopNavigation(page, loggedInUser));
//	}	
//	
//	protected void removeModelAttributes(ModelMap map) {
//		// Eliminate existing model attributes, otherwise they get added by Spring
//		// to the redirect URL
//		map.remove("userHasAgentRole");
//		map.remove("userHasAdminRole");
//		map.remove("userHasUserAdminRole");
//	}
//	
//	protected String getServerHostname() {
//		Config cfg = this.configDao.getConfig(ConfigAttr.SERVER_HOSTNAME);
//		return cfg != null ? cfg.getValue() : "www.slepeweb.com";
//	}
}
