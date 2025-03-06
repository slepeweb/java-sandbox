package com.slepeweb.site.control;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.LoginSupport;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.service.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LoginController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(LoginController.class);
	
	@Autowired private LoginService loginService;
	
	public String login (
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) throws IOException {					
		
		if (req.getMethod().equalsIgnoreCase("post")) {
			String alias = req.getParameter("alias");
			StringBuffer msg = new StringBuffer(String.format("User '%s' logging in ... ", alias));
			
			String password = req.getParameter("password");
			String originalPath = req.getParameter("originalPath");
			LoginSupport supp = this.loginService.login(alias, password, req);
			
			if (supp.isSuccess()) {
				msg.append("success!");
				String path = originalPath;
				if (StringUtils.isBlank(path)) {
					path = "/";
				}
				res.sendRedirect(path);
			}
			else {
				model.addAttribute("error", supp.getUserMessage());
				msg.append("FAILED: " + supp.getUserMessage());
			}
			
			LOG.info(msg);
			
		}
		else if (req.getMethod().equalsIgnoreCase("get")) {
			if (req.getParameter("logout") != null) {
				this.loginService.logout(req);
			}
		}
		
		return composeJspPath(shortSitename, "login"); 
	}
		
}
