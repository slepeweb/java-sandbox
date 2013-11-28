package com.slepeweb.sandbox.www.control;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.slepeweb.sandbox.orm.Role;
import com.slepeweb.sandbox.orm.User;
import com.slepeweb.sandbox.orm.UserDao;
import com.slepeweb.sandbox.www.model.Page;

@Controller
@RequestMapping(value = "/user")
public class UserController extends BaseController {

	@Autowired
	private UserDao userDao;
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addUser(@ModelAttribute("_user") User loggedInUser, ModelMap model) {
		Page page = new Page().
			setHref("/user/add").
			setTitle("Users").
			setView("userForm").
			addRole(Role.USER_ADMIN_ROLE).
			addStylesheet("/resources/css/slepeweb.css");
		
		page.setTopNavigation(getTopNavigation(page, loggedInUser));

		if (page.isAccessibleBy(loggedInUser)) {
			model.addAttribute("userForm", new User());
			model.addAttribute("availableRoles", this.userDao.getAvailableRoles());
		}
		
		return checkAccessibility(page, loggedInUser, model);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addUser(@ModelAttribute("userForm") User userForm, ModelMap model, BindingResult result) {
		if (! result.hasErrors()) {
			userForm.encryptPassword();
			
			// Convert rolesStr to an array of Roles
			Role r;
			for (String roleStr : userForm.getSelectedRoles()) {
				r = this.userDao.getRole(Integer.valueOf(roleStr.trim()));
				if (r != null) {
					userForm.getRoles().add(r);
				}
			}
			
			this.userDao.addUser(userForm);
			
			// TODO: redirects need attention for Production
			removeModelAttributes(model);
			return "redirect:/user/list";
		}
		
		model.addAttribute("availableRoles", this.userDao.getAvailableRoles());
		return "userForm";
	}

	@RequestMapping("/list")
	public String listUsers(@ModelAttribute("_user") User loggedInUser, ModelMap model) {
		Page page = new Page().
			setHref("/user/list").
			setTitle("Users").
			setView("userList").
			addRole(Role.USER_ADMIN_ROLE).
			addStylesheet("/resources/css/slepeweb.css");
		
		page.setTopNavigation(getTopNavigation(page, loggedInUser));

		if (page.isAccessibleBy(loggedInUser)) {
			model.addAttribute("userList", this.userDao.getAllUsers());
		}
		
		return checkAccessibility(page, loggedInUser, model);
	}
	
	@RequestMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, ModelMap model) {
		
		this.userDao.deleteUser(id);

		// TODO: redirects need attention for Production
		removeModelAttributes(model);
		return "redirect:/user/list";
	}
	
	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	public String updateUser(@ModelAttribute("_user") User loggedInUser, 
			@PathVariable("id") Integer id, ModelMap model) {
		
		Page page = new Page().
			setHref("/user/update").
			setTitle("Users").
			setView("userForm").
			addRole(Role.USER_ADMIN_ROLE).
			addStylesheet("/resources/css/slepeweb.css");
		
		page.setTopNavigation(getTopNavigation(page, loggedInUser));

		if (page.isAccessibleBy(loggedInUser)) {
			User target = this.userDao.getUser(id);
			roles2Selected(target);
			model.addAttribute("userForm", target);
			model.addAttribute("availableRoles", this.userDao.getAvailableRoles());
		}
		
		return checkAccessibility(page, loggedInUser, model);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateUser(@ModelAttribute("userForm") User userForm,
			ModelMap model, BindingResult result) {
		
		if (! result.hasErrors()) {
			selected2Roles(userForm);
			userForm.encryptPassword();
			this.userDao.updateUser(userForm);
							
			// TODO: redirects need attention for Production
			removeModelAttributes(model);
			return "redirect:/user/list";
		}
		
		model.addAttribute("availableRoles", this.userDao.getAvailableRoles());
		return "userForm";
	}
	
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
