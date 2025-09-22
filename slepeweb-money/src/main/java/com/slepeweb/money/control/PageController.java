package com.slepeweb.money.control;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.common.bean.MoneyDashboard;
import com.slepeweb.common.service.SendMailService;
import com.slepeweb.money.Util;
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
		model.addAttribute("_dash", buildDashboard());
		return "dashboard";
	}
	
	private Dashboard buildDashboard() {
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
		
		return dash;
	}
	
	@RequestMapping(value="/summary", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public MoneyDashboard summary( 
			@RequestParam(value="alias", required = true) String alias,
			@RequestParam(value="password", required = true) String password,
			ModelMap model) {
		
		LoginResponse login = this.loginService.login(alias, password);
		
		if (login.isSuccess()) {			
			Dashboard dash = buildDashboard();
			return dash.adapt();
		}
		
		return new MoneyDashboard().setError(login.getErrorMessage());
	}
	

	@RequestMapping(value="/notfound")	
	public String notfound(ModelMap model) { 
		return dashboard(model);
	}

	@RequestMapping(value="/login")
	public String loginForm(
		HttpServletRequest req,
		@RequestParam(value="flash", required = false) String flash,
		@RequestParam(value="logout", required = false) String logout,
		ModelMap model) {
 
		if (logout != null) {
			req.getSession().removeAttribute(User.USER_ATTR);
		}
 
		LocalDateTime d = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		String s = d.format(dtf);
		model.addAttribute("_now", s);
		
		return "loginForm"; 
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public RedirectView loginSubmission (
			HttpServletRequest req,
			HttpServletResponse res,
			@RequestParam(value="alias", required = true) String alias,
			@RequestParam(value="password", required = true) String password,
			ModelMap model) throws IOException {
			
		LoginResponse resp = this.loginService.login(alias, password);
		String from = "george.buttigieg56@gmail.com";
		String to = "george@buttigieg.org.uk";
		String name = "George Buttigieg";
		String msg;
		
		if (resp.isSuccess()) {
			if (resp.isSendEmail()) {
				msg = String.format("User %s successfully logged in to Money", alias);
				this.sendMailService.sendMail(from, to, name, "Successful login", msg);
			}
			
			req.getSession().setAttribute(User.USER_ATTR, resp.getUser());			
			return new RedirectView(String.format("%s/", req.getContextPath()));

		}
		
		msg = String.format("A user failed to login to the Money app. Credentials:\n\nusername: %s\npassword: %s", 
				alias, password);
		
		this.sendMailService.sendMail(from, to, name,
				"***Failed*** login",
				msg);
		
		return new RedirectView(String.format("%s/login?flash=%s", req.getContextPath(), 
				Util.encodeUrl("failure|" + resp.getErrorMessage())));
	}
		
}