package com.slepeweb.site.pho.control;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.LoginSupport;
import com.slepeweb.cms.service.LoginService;
import com.slepeweb.site.control.BaseController;

@Controller
@RequestMapping("/spring/pho")
public class LoginController extends BaseController {
	
	//private static Logger LOG = Logger.getLogger(LoginController.class);
	public static final String USER_ATTR = "_user";
	public static final String ADMIN_EMAIL = "admin@buttigieg.org.uk";
	
	@Autowired private LoginService loginService;
	
	@RequestMapping(value="/login")
	public String login (
			@ModelAttribute(ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) throws IOException {	
				
		if (req.getMethod().equalsIgnoreCase("post")) {
			String email = req.getParameter("email");
			String password = req.getParameter("password");
			String originalPath = req.getParameter("originalPath");
			LoginSupport supp = this.loginService.login(email, password, req);
			
			if (supp.isSuccess()) {
				String path = originalPath;
				if (StringUtils.isBlank(path)) {
					path = "/";
				}
				res.sendRedirect(path);
			}
			else {
				model.addAttribute("error", supp.getErrorMessage());
			}
			
		}
		else if (req.getMethod().equalsIgnoreCase("get")) {
			if (req.getParameter("logout") != null) {
				this.loginService.logout(req);
			}
		}
		
		return composeJspPath(shortSitename, "login"); 
	}
		
}
