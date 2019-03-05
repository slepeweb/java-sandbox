package com.slepeweb.money.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.service.AccountService;

@Controller
public class PageController extends BaseController {
	
	@Autowired private AccountService accountService;
	
	@RequestMapping(value="/")	
	public String dashboard(ModelMap model) { 
		List<Account> all = this.accountService.getAllWithBalances();
		String lastType = null;
		List<Pair<String, Long>> summary = new ArrayList<Pair<String, Long>>();
		int index = -1;
		long total = 0L, grandTotal = 0L;
		
		for (Account a : all) {
			if (lastType == null || ! lastType.equals(a.getType())) {
				index++;
				grandTotal += total;
				total = 0;
				lastType = a.getType();
				summary.add(Pair.of("dummy", 0L));
			}
			
			total += a.getBalance();
			summary.set(index, Pair.of(a.getType(), total));
		}
		
		grandTotal += total;
		model.addAttribute("_accounts", all);
		model.addAttribute("_summary", summary);
		model.addAttribute("_grandTotal", grandTotal);
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