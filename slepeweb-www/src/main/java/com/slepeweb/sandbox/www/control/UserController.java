package com.slepeweb.sandbox.www.control;

import java.util.HashSet;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.slepeweb.sandbox.orm.Role;
import com.slepeweb.sandbox.orm.User;
import com.slepeweb.sandbox.orm.UserDao;
import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.service.NavigationService;
import com.slepeweb.sandbox.www.service.PageService;

@Controller
@RequestMapping(value = "/sandbox/user")
public class UserController extends BaseController {

	@Autowired
	private PageService pageService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private NavigationService navigationService;
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public String index(HttpSession session, ModelMap model) {
		return introPage(session, model);
	}
	
	@RequestMapping(value = "add", method = RequestMethod.GET)
	public String addUser(HttpSession session, ModelMap model) {
		User loggedInUser = getLoggedInUser(session);
		Page page = getUserFormPage(loggedInUser);

		if (page.isAccessibleBy(loggedInUser)) {
			model.addAttribute("userForm", new User());
			model.addAttribute("availableRoles", this.userDao.getAvailableRoles());
		}
		
		return checkAccessibility(page, loggedInUser, model);
	}
	
	private Page getUserFormPage(User loggedInUser) {
		Page page = this.pageService.getPage(PageService.USER_ADD);		
		page.setTopNavigation(getTopNavigation(page, loggedInUser));
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));
		return page;
	}

	/*
	 * The BindingResult parameter MUST follow the object being (data-) bound.
	 */
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public String addUser(HttpSession session, 
			@Valid @ModelAttribute("userForm") User userForm, BindingResult result, 
			ModelMap model, RedirectAttributes rattr) {
		
		// Need to validate user here, since it is optional when the form is used to update an existing user
		if (StringUtils.isBlank(userForm.getPassword())) {
			/* 
			 * NB. If you don't specify the rejected value in the FieldError,
			 * then the bad field value is not displayed when the form is re-rendered
			 */
			String[] empty = new String[] {};
			result.addError(new FieldError("userForm", "password", userForm.getPassword(), false, 
					empty, empty,
					"Please enter a password"));
		}
		
		User loggedInUser = getLoggedInUser(session);

		if (! result.hasErrors()) {
			if (hasRole(loggedInUser, Role.USER_ADMIN_ROLE)) {
				userForm.encryptPasswordIfNotBlank();
				
				selected2Roles(userForm);
				this.userDao.addUser(userForm);
				
				rattr.addFlashAttribute("_flashMsg", 
						String.format("User [%s] successfully added", userForm.getName()));
			}
			else {
				rattr.addFlashAttribute("_flashError", "You have insufficient privileges to add a user");
			}

			removeModelAttributes(model);
			return String.format("redirect://%s/%s", getServerHostname(), "sandbox/user/list");
		}
		
		Page page = getUserFormPage(loggedInUser);
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));
		model.addAttribute("_page", page);
		model.addAttribute("availableRoles", this.userDao.getAvailableRoles());
		return page.getView();
	}

	@RequestMapping("list")
	public String listUsers(HttpSession session, ModelMap model) {
		Page page = this.pageService.getPage(PageService.USER_LIST);				
		User loggedInUser = getLoggedInUser(session);
		page.setTopNavigation(getTopNavigation(page, loggedInUser));
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));

		if (page.isAccessibleBy(loggedInUser)) {
			boolean onlyDemoUsers = ! (loggedInUser != null && 
					loggedInUser.hasRole(Role.USER_ADMIN_ROLE));
			model.addAttribute("userList", this.userDao.getAllUsers(onlyDemoUsers));
		}
		
		return checkAccessibility(page, loggedInUser, model);
	}
	
	@RequestMapping("delete/{id}")
	public String deleteUser(HttpSession session, 
			@PathVariable("id") Integer id, 
			ModelMap model, RedirectAttributes rattr) {
		
		User loggedInUser = getLoggedInUser(session);

		if (hasRole(loggedInUser, Role.USER_ADMIN_ROLE)) {
			this.userDao.deleteUser(id);
			rattr.addFlashAttribute("_flashMsg", "User successfully deleted");
		}
		else {
			rattr.addFlashAttribute("_flashError", "You have insufficient privileges to delete a user");
		}

		removeModelAttributes(model);
		return String.format("redirect://%s/%s", getServerHostname(), "sandbox/user/list");
	}
	
	@RequestMapping(value = "update/{id}", method = RequestMethod.GET)
	public String updateUser(HttpSession session, 
			@PathVariable("id") Integer id, ModelMap model) {
		
		Page page = this.pageService.getPage(PageService.USER_UPDATE);				
		User loggedInUser = getLoggedInUser(session);
		page.setTopNavigation(getTopNavigation(page, loggedInUser));
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));

		if (page.isAccessibleBy(loggedInUser)) {
			User target = this.userDao.getUser(id);
			roles2Selected(target);
			model.addAttribute("userForm", target);
			model.addAttribute("availableRoles", this.userDao.getAvailableRoles());
		}
		
		return checkAccessibility(page, loggedInUser, model);
	}

	/*
	 * The BindingResult parameter MUST follow the object being (data-) bound.
	 */
	@RequestMapping(value="update", method=RequestMethod.POST)
	public String updateUser(HttpSession session,
			@Valid @ModelAttribute("userForm") User userForm, BindingResult result, 
			ModelMap model, RedirectAttributes rattr) {
		
		/*
		 * The password property on userForm may be blank, in which case no attempt will
		 * be made to change the encrypted value - the original encryped value will be
		 * obtained from the hidden input field on the form.
		 */
		User loggedInUser = getLoggedInUser(session);
		if (! result.hasErrors()) {
			if (hasRole(loggedInUser, Role.USER_ADMIN_ROLE)) {
				selected2Roles(userForm);
				userForm.encryptPasswordIfNotBlank();
				this.userDao.updateUser(userForm);
								
				rattr.addFlashAttribute("_flashMsg", 
						String.format("User [%s] successfully updated", userForm.getName()));
			}
			else {
				rattr.addFlashAttribute("_flashError", "You have insufficient privileges to update a user");
			}

			removeModelAttributes(model);
			return String.format("redirect://%s/%s", getServerHostname(), "sandbox/user/list");
		}
		
		Page page = getUserFormPage(loggedInUser);
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));
		model.addAttribute("_page", page);
		model.addAttribute("availableRoles", this.userDao.getAvailableRoles());
		return page.getView();
	}
	
	@RequestMapping(value="intro", method=RequestMethod.GET)
	public String introPage(HttpSession session, ModelMap model) {
		Page page = this.pageService.getPage(PageService.USER_INTRO);					
		User loggedInUser = getLoggedInUser(session);
		page.setTopNavigation(getTopNavigation(page, loggedInUser));
		page.getLeftSidebar().setNavigation(this.navigationService.getSandboxNavigation(page));
		model.addAttribute("_page", page);
		return page.getView();
	}
	
	private boolean hasRole(User u, String role) {
		return u != null && u.hasRole(role);
	}
	
	/*
	 * The next 2 methods copy data between the roles (Set) property and
	 * the selectedRoles (String[]) property. The hibernate mapping required a Set of
	 * roles to be defined in the User entity, but the Spring form data-binding returns
	 * a String[] of options selected from a <select multiple="true"> drop-down.
	 */
	
	private void roles2Selected(User u) {
		u.setSelectedRoles(new String[u.getRoles().size()]);
		int i = 0;
		for (Role r : u.getRoles()) {
			u.getSelectedRoles()[i++] = r.getName();
		}
	}
	
	private void selected2Roles(User u) {
		u.setRoles(new HashSet<Role>(u.getSelectedRoles().length));
		Role r;
		
		for (String name : u.getSelectedRoles()) {
			r = this.userDao.getRole(name);
			if (r != null) {
				u.getRoles().add(r);
			}
		}
	}
}
