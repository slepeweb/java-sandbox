package com.slepeweb.money.control;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.TransactionService;

@Controller
@RequestMapping("/account")
public class AccountController extends BaseController {
	
	@Autowired private AccountService accountService;
	@Autowired private TransactionService transactionService;
	
	@RequestMapping(value="/list")	
	public String list(ModelMap model) { 
		List<Account> open = this.accountService.getAll(false);
		List<Account> all = this.accountService.getAll(true);
		
		// Remove all open accounts from the 'all' collection
		Iterator<Account> iter = all.iterator();
		Account a;
		while (iter.hasNext()) {
			a = iter.next();
			if (! a.isClosed()) {
				iter.remove();
			}
		}
		
		model.addAttribute("_openAccounts", open);
		model.addAttribute("_closedAccounts", all);
		return "accountList";
	}
		
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String addForm(ModelMap model) {
		
		model.addAttribute("_account", new Account());
		model.addAttribute("_formMode", "add");
		return "accountForm";
	}
	
	@RequestMapping(value="/form/{accountId}", method=RequestMethod.GET)
	public String updateForm(@PathVariable long accountId, ModelMap model) {
		
		model.addAttribute("_account", this.accountService.get(accountId));
		model.addAttribute("_formMode", "update");
		model.addAttribute("_numDeletableTransactions", this.transactionService.getNumTransactionsForAccount(accountId));
		return "accountForm";
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public RedirectView update(HttpServletRequest req, ModelMap model) {
		
		String flash;	
		boolean isUpdateMode = req.getParameter("formMode").equals("update");
		
		Account a = new Account().
				setId(Long.valueOf(req.getParameter("id"))).
				setName(req.getParameter("name")).
				setType(req.getParameter("type")).
				setClosed(! req.getParameter("status").equals("open")).
				setOpeningBalance(Util.parsePounds(req.getParameter("opening"))).
				setNote(req.getParameter("note"));
		
		try {
			this.accountService.save(a);
			flash = String.format("success|Account successfully %s", isUpdateMode ? "updated" : "added");
		}
		catch (Exception e) {
			flash = String.format("failure|Failed to %s account", isUpdateMode ? "update" : "add new");
		}
	
		return new RedirectView(String.format("%s/account/form/%d?flash=%s", 
				req.getContextPath(), a.getId(), Util.encodeUrl(flash)));
	}
	
	@RequestMapping(value="/delete/{accountId}", method=RequestMethod.GET)
	public RedirectView delete(@PathVariable long accountId, HttpServletRequest req, ModelMap model) {
		
		String flash;		
		User u = (User) model.get(USER);
		long numDeletables = this.transactionService.getNumTransactionsForAccount(accountId);
		
		if ((u != null && u.getUsername().equals("MONEY_ADMIN")) || numDeletables == 0) {		
			try {
				this.accountService.delete(accountId);
				flash="success|Account successfully deleted";
			}
			catch (Exception e) {
				flash="failure|Failed to delete account";
			}
		}
		else {
			flash = "failure|Failed to delete account - authorisation failure";		
		}
		
		return new RedirectView(String.format("%s/account/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}	
}