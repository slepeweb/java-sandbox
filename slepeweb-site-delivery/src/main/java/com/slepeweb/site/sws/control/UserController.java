package com.slepeweb.site.sws.control;

import java.util.HashSet;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.sws.orm.Role;
import com.slepeweb.site.sws.orm.User;
import com.slepeweb.site.sws.orm.UserDao;

@Controller
@RequestMapping(value = "/spring/user")
public class UserController extends BaseController {

	@Autowired private UserDao userDao;
	@Autowired private CmsService cmsService;
	@Autowired private StandardPasswordEncoder passwordEncoder;
	
	@RequestMapping("list")
	public String listUsers(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {
		
		Page page = getStandardPage(i, shortSitename, "user/list", model);
		page.setLeftNavigation();
		model.addAttribute("userList", this.userDao.getAllUsers(true));		
		return page.getView();
	}
	
	@RequestMapping(value = "form", method = RequestMethod.GET)
	public String showForm(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@RequestParam(value="userId", required=false) Integer userId,
			ModelMap model) {
		
		Page page = getStandardPage(i, shortSitename, "user/form", model);
		page.setLeftNavigation();
		
		if (userId == null) {
			model.addAttribute("user", new User().setUserFormPageId(i.getId()));
		}
		else {
			User target = this.userDao.getUser(userId);
			target.setUserFormPageId(i.getId());
			roles2Selected(target);
			model.addAttribute("user", target);
		}
		
		model.addAttribute("availableRoles", this.userDao.getAvailableRoles());
		return page.getView();
	}
	
	/*
	private Page getFormPage(Item i, String shortSitename, Integer userId, ModelMap model) {
		Page page = getStandardPage(i, shortSitename, "user/form", model);
		page.setLeftNavigation();
		
		if (userId == null) {
			model.addAttribute("user", new User().setUserFormPageId(i.getId()));
		}
		else {
			User targ		Page page = getStandardPage(i, shortSitename, "user/form", model);
		page.setLeftNavigation();
		
		if (userId == null) {
			model.addAttribute("user", new User().setUserFormPageId(i.getId()));
		}
		else {
			User target = this.userDao.getUser(userId);
			target.setUserFormPageId(i.getId());
			roles2Selected(target);
			model.addAttribute("user", target);
		}
		
		model.addAttribute("availableRoles", this.userDao.getAvailableRoles());
		return page.getView();
et = this.userDao.getUser(userId);
			target.setUserFormPageId(i.getId());
			roles2Selected(target);
			model.addAttribute("user", target);
		}
		
		model.addAttribute("availableRoles", this.userDao.getAvailableRoles());
		return page;
	}
	*/
	
	/*
	 * The BindingResult parameter MUST follow the object being (data-) bound.
	 */
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public String addUser(
			@Valid @ModelAttribute("user") User userForm, BindingResult result, 
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
		
		if (! result.hasErrors()) {
			userForm.encryptPasswordIfNotBlank(this.passwordEncoder);			
			selected2Roles(userForm);
			this.userDao.addUser(userForm);
			
			rattr.addFlashAttribute("_flashMsg", 
					String.format("User [%s] successfully added", userForm.getName()));

			return "redirect:/sandbox/hibernate/list";
		}
		
		Item i = this.cmsService.getItemService().getItem(new Long(userForm.getUserFormPageId()));
		Page page = getStandardPage(i, i.getSite().getShortname(), "user/form", model);
		page.setLeftNavigation();				
		return page.getView();
	}

	/*
	 * The BindingResult parameter MUST follow the object being (data-) bound.
	 */
	@RequestMapping(value="upd", method=RequestMethod.POST)
	public String updateUser(
			@ModelAttribute("user") @Valid User userForm, BindingResult result, 
			ModelMap model, RedirectAttributes rattr) {
		
		/*
		 * The password property on userForm may be blank, in which case no attempt will
		 * be made to change the encrypted value - the original encryped value will be
		 * obtained from the hidden input field on the form.
		 */
		if (! result.hasErrors()) {
			selected2Roles(userForm);
			userForm.encryptPasswordIfNotBlank(this.passwordEncoder);
			this.userDao.updateUser(userForm);
							
			rattr.addFlashAttribute("_flashMsg", 
					String.format("User [%s] successfully updated", userForm.getName()));

			return "redirect:/sandbox/hibernate/list";
		}
		
		Item i = this.cmsService.getItemService().getItem(new Long(userForm.getUserFormPageId()));
		Page page = getStandardPage(i, i.getSite().getShortname(), "user/form", model);
		page.setLeftNavigation();				
		return page.getView();
	}
	
	
	@RequestMapping("del/{id}")
	public String deleteUser( 
		@PathVariable("id") Integer id, 
		ModelMap model, RedirectAttributes rattr) {
	
		this.userDao.deleteUser(id);
		rattr.addFlashAttribute("_flashMsg", "User successfully deleted");

		return "redirect:/sandbox/hibernate/list";
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