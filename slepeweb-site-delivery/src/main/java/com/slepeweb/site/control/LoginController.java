package com.slepeweb.site.control;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.LoginSupport;
import com.slepeweb.cms.bean.QandAList;
import com.slepeweb.cms.bean.SiteConfigCache;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.constant.SiteConfigKey;
import com.slepeweb.cms.service.LoginService;
import com.slepeweb.cms.service.QandAService;
import com.slepeweb.site.model.Page;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/spring/common")
public class LoginController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(LoginController.class);
	
	@Autowired private LoginService loginService;
	@Autowired private SiteConfigCache siteConfigCache;
	@Autowired private QandAService qandAService;
	
	@RequestMapping(value="/login")
	public String login (
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) throws IOException {					
		
		String redirectPath = null;
		
		if (req.getMethod().equalsIgnoreCase("post")) {
			String alias = req.getParameter("alias");
			StringBuffer msg = new StringBuffer(String.format("User '%s' logging in ... ", alias));
			
			String password = req.getParameter("password");
			redirectPath = req.getParameter("redirectPath");
			LoginSupport supp = this.loginService.login(alias, password, req);
			
			if (supp.isSuccess()) {
				msg.append("success!");
				String path = redirectPath;
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
			
			redirectPath = req.getParameter("redirectPath");
			if (redirectPath == null) {
				redirectPath = "";
			}
			model.addAttribute("_redirectPath", redirectPath);
			model.addAttribute("_forgottenPasswordFormHref", 
					i.getSite().getEditorialHost().getNamePortAndProtocol() + "/cms/user/forgot/password");
			
			if (req.getParameter("logout") != null) {
				this.loginService.logout(req);
			}
		}
		
		return composeJspPath(shortSitename, "login"); 
	}
		
	@RequestMapping(value="/superlogin")	
	public String superLogin(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) throws Exception {	
		
		User u = getUser(req);
		QandAList qalStoredInDb = this.qandAService.getQandAList(u);
		String targetOnSuccess = req.getParameter("success");
		int minQandA = this.siteConfigCache.getIntValue(i.getSite().getId(), SiteConfigKey.NUM_QANDA_MIN, 2);
				
		if (req.getMethod().equalsIgnoreCase("get")) {
			if (qalStoredInDb.getSize() < minQandA) {
				model.addAttribute("error", 
						String.format("User '%s' has not recorded sufficient security answers - abort", u.getFullName()));
			}
			else {
				model.addAttribute("_qal", qalStoredInDb);
				model.addAttribute("_success", targetOnSuccess);
			}
			
			Page page = getStandardPage(i, shortSitename, "superLogin", model);
			return page.getView();
		}
		
		// Dealing with form submission ...
		QandAList qalProvidedInForm = new QandAList().fillFromRequest(req, qalStoredInDb.getSize());
		
		if (qalProvidedInForm.equals(qalStoredInDb)) {
			req.getSession().setAttribute(AttrName.SUPER_USER, u);
			res.sendRedirect(targetOnSuccess);
			LOG.info(String.format("User '%s' correctly answered %d security questions", u.getFullName(), qalStoredInDb.getSize()));
			return null;
		}
		
		LOG.info(String.format("User '%s' FAILED to correctly answer %d security questions", u.getFullName(), qalStoredInDb.getSize()));
		String path = this.siteConfigCache.getValue(i.getSite().getId(), "path.superlogin", "superlogin");
		String loginFormUrl = String.format("%s?error=%s&success=%s", path, "Invalid+credentials", targetOnSuccess);
		res.sendRedirect(loginFormUrl);
		return null;
	}

}
