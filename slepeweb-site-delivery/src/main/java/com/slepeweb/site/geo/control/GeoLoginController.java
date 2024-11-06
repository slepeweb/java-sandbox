package com.slepeweb.site.geo.control;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.site.control.LoginController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/spring/geo")
public class GeoLoginController extends LoginController {
	
	@RequestMapping(value="/login")
	public String login (
			@ModelAttribute(ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) throws IOException {	
				
		return super.login(i, shortSitename, req, res, model);
	}
		
}
