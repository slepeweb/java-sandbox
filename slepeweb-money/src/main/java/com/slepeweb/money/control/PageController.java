package com.slepeweb.money.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.SolrService;
import com.slepeweb.money.service.TransactionService;

@Controller
public class PageController extends BaseController {
	
	@Autowired private AccountService accountService;
	@Autowired private TransactionService transactionService;
	@Autowired private SolrService solrService;
	
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
		
	@RequestMapping(value="/index")	
	public RedirectView indexEverything(HttpServletRequest req) { 
		this.solrService.removeAllTransactions();
		this.solrService.save(this.transactionService.getAll());
		return new RedirectView(String.format("%s/search/?flash=%s", 
				req.getContextPath(), Util.encodeUrl("success|Indexing complete")));
	}
	
	@RequestMapping(value="/index/{accountId}")	
	public RedirectView index(@PathVariable long accountId, HttpServletRequest req) { 
		Account a = this.accountService.get(accountId);
		this.solrService.removeTransactionsByAccount(a.getName());
		this.solrService.save(this.transactionService.getTransactionsForAccount(accountId));
		return new RedirectView(String.format("%s/search/?flash=%s", 
				req.getContextPath(), Util.encodeUrl("success|Indexing complete")));
	}
	
}