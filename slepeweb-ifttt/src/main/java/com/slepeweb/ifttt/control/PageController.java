package com.slepeweb.ifttt.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController extends BaseController {
	
	@RequestMapping(value="/")	
	public String standardPage(ModelMap model) { 
		return "standardPage";
	}

	@RequestMapping(value="/notfound")	
	public String notfound(ModelMap model) { 
		return standardPage(model);
	}

	@RequestMapping(value="/login")
	public String loginForm(
		@RequestParam(value="error", required = false) String error,
		@RequestParam(value="logout", required = false) String logout,
		ModelMap model) {
 
		if (error != null) {
			model.addAttribute("error", "Invalid username and password!");
		}
 
		if (logout != null) {
			model.addAttribute("msg", "You've been successfully logged out.");
		}
 
		return "loginForm"; 
	}
		
}