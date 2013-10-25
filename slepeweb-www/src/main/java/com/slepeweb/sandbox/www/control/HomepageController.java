package com.slepeweb.sandbox.www.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.slepeweb.sandbox.www.model.Page;
import com.slepeweb.sandbox.www.service.NavigationService;

@Controller
public class HomepageController {
	
	@Autowired
	private NavigationService navigationService;
	
	@RequestMapping(value = { "/home", "/homepage" })
	public ModelAndView doGeneric() {
		ModelAndView modelAndView = new ModelAndView("home");
		Page page = new Page().setPath("/home");
		modelAndView.addObject("_page", page);
		
		page.getHeader().setTopNavigation(this.navigationService.getTopNavigation(page));
		return modelAndView;
	}
}
