package com.slepeweb.money.control;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.common.service.SendMailService;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Dashboard;
import com.slepeweb.money.bean.DashboardAccountGroup;
import com.slepeweb.money.bean.LoginResponse;
import com.slepeweb.money.bean.User;
import com.slepeweb.money.service.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PageController extends BaseController {
	
	@Autowired private LoginService loginService;
	@Autowired private SendMailService sendMailService;
	
	@RequestMapping(value="/")	
	public String dashboard(ModelMap model) { 
		Dashboard dash = new Dashboard();
		DashboardAccountGroup group;
		List<Account> all = this.accountService.getAllWithBalances();
		
		for (Account a : all) {
			// Not interested in 'other' accounts, ie not to be included in the summary or asset history
			if (a.getType() != null && ! a.getType().equals("other")) {
				group = dash.addIfMissing(a.getType());
				group.getAccounts().add(a);
			}
		}
		
		model.addAttribute("_dash", dash);
		return "dashboard";
	}

	@RequestMapping(value="/notfound")	
	public String notfound(ModelMap model) { 
		return dashboard(model);
	}

	@RequestMapping(value="/login")
	public String loginForm(
		HttpServletRequest req,
		@RequestParam(value="error", required = false) String error,
		@RequestParam(value="logout", required = false) String logout,
		ModelMap model) {
 
		if (error != null) {
			model.addAttribute("error", "Invalid username and password!");
		}
 
		if (logout != null) {
			req.getSession().removeAttribute(User.USER_ATTR);
			model.addAttribute("msg", "You've been successfully logged out.");
		}
 
		return "loginForm"; 
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String loginSubmission (
			HttpServletRequest req,
			HttpServletResponse res,
			@RequestParam(value="alias", required = true) String alias,
			@RequestParam(value="password", required = true) String password,
			ModelMap model) throws IOException {
			
		LoginResponse resp = this.loginService.login(alias, password);
		String from = "george.buttigieg56@gmail.com";
		String to = "george@buttigieg.org.uk";
		String name = "George Buttigieg";
		
		if (resp.isSuccess()) {
			this.sendMailService.sendMail(from, to, name,
					"Successful login to Money",
					"(No body)");
			
			req.getSession().setAttribute(User.USER_ATTR, resp.getUser());
			return dashboard(model);
		}
		
		String msg = String.format("A user failed to login, using the following details:\n\nusername: %s\npassword: %s", 
				alias, password);
		
		this.sendMailService.sendMail(from, to, name,
				"***Failed*** login to Money",
				msg);
		
		res.sendRedirect(String.format("%s/login?error=%s", req.getContextPath(), resp.getErrorMessage()));
		return null;
	}
		
}