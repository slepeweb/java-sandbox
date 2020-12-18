package com.slepeweb.money.control;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Dashboard;
import com.slepeweb.money.bean.DashboardAccountGroup;

@Controller
public class PageController extends BaseController {
	
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