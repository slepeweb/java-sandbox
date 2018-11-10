package com.slepeweb.money.control;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.MonthPager;
import com.slepeweb.money.bean.NamedList;
import com.slepeweb.money.bean.NormalisedMonth;
import com.slepeweb.money.bean.Pager;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.RunningBalance;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.TransactionList;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.TransactionService;
import com.slepeweb.money.service.Util;

@Controller
public class PageController extends BaseController {
	
	private static final String PAYEE_SEARCH = "_payeeSearch";
	private static final String CATEGORY_SEARCH = "_categorySearch";
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private TransactionService transactionService;
	
	@RequestMapping(value="/category/list")	
	public String categoryList(ModelMap model) { 
		List<NamedList<Category>> categories = new ArrayList<NamedList<Category>>();
		NamedList<Category> mapping = null;
		String lastName = null, nextName = null;
		List<Category> all = this.categoryService.getAll();
		
		for (Category c : all) {
			if (c.getMajor().length() == 0) {
				// This happens, not sure why
				c.setMajor("(no major category)");
			}
			
			if (c.getMinor().length() == 0) {
				// This happens, not sure why
				c.setMinor("(no minor category)");
			}
			
			nextName = c.getMajor();
			if (lastName == null || ! lastName.equals(nextName)) {
				mapping = new NamedList<Category>(nextName, new ArrayList<Category>());
				categories.add(mapping);
				lastName = nextName;
			}
			
			mapping.getObjects().add(c);
		}
		
		model.addAttribute("_categories", categories);
		model.addAttribute("_count", all.size());
		return "categoryList";
	}
	
	@RequestMapping(value="/payee/list")	
	public String payeeList(ModelMap model) { 
		List<NamedList<Payee>> payees = new ArrayList<NamedList<Payee>>();
		NamedList<Payee> mapping = null;
		String lastName = null, nextName = null;
		List<Payee> all = this.payeeService.getAll();
		
		for (Payee p : all) {
			if (p.getName().length() == 0) {
				// This happens, not sure why
				continue;
			}
			
			nextName = p.getName().substring(0, 1).toUpperCase();
			if (StringUtils.isNumeric(nextName)) {
				nextName = "0";
			}
			
			if (lastName == null || ! lastName.equals(nextName)) {
				mapping = new NamedList<Payee>(nextName, new ArrayList<Payee>());
				payees.add(mapping);
				lastName = nextName;
			}
			
			mapping.getObjects().add(p);
		}
		
		model.addAttribute("_payees", payees);
		model.addAttribute("_count", all.size());
		return "payeeList";
	}
	
	@RequestMapping(value="/account/list")	
	public String accountList(ModelMap model) { 
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
	
	@RequestMapping(value="/transaction/list")	
	public String transactionListNoAccount(ModelMap model) { 
		List<Account> allAccounts = this.accountService.getAll(false);
		if (allAccounts.size() > 0) {
			return transactionListNoMonth(allAccounts.get(0).getId(), model);
		}
		return null;
	}
	
	@RequestMapping(value="/transaction/list/{accountId}")	
	public String transactionListNoMonth(@PathVariable long accountId, ModelMap model) { 
		Timestamp end = this.transactionService.getTransactionDateForAccount(accountId, false);		
		NormalisedMonth endMonth = new NormalisedMonth(end);
		return transactionList(accountId, endMonth.getIndex(), model);
	}
	
	@RequestMapping(value="/transaction/list/{accountId}/{selectedMonthIndex}")	
	public String transactionList(@PathVariable long accountId, @PathVariable int selectedMonthIndex, ModelMap model) {
		
		Account a = this.accountService.get(accountId);
		List<Account> allAccounts = this.accountService.getAll(false);
		
		if (a == null && allAccounts.size() > 0) {
			a = allAccounts.get(0);
		}
		
		NormalisedMonth firstMonth = new NormalisedMonth(this.transactionService.getTransactionDateForAccount(accountId, true));
		NormalisedMonth lastMonth = new NormalisedMonth(this.transactionService.getTransactionDateForAccount(accountId, false));
		NormalisedMonth selectedMonth = new NormalisedMonth(selectedMonthIndex);

		if (selectedMonth.isBefore(firstMonth)) {
			selectedMonth.set(firstMonth);
		}
		
		if (selectedMonth.isAfter(lastMonth)) {
			selectedMonth.set(lastMonth);
		}
			
		MonthPager p = new MonthPager(firstMonth, selectedMonth, lastMonth);
		
		Calendar monthEnd = Util.today();
		monthEnd.add(Calendar.MONTH, selectedMonth.getCalendarOffset());
		monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getMaximum(Calendar.DAY_OF_MONTH));
		
		// Wind down month end for shorter months
		int day = monthEnd.get(Calendar.DATE);
		if (day < 4) {
			monthEnd.add(Calendar.DATE, -day);
		}
		
		Calendar monthBeginning = Util.today();
		monthBeginning.add(Calendar.MONTH, selectedMonth.getCalendarOffset());
		monthBeginning.set(Calendar.DAY_OF_MONTH, 1);
		
		Timestamp from = new Timestamp(monthBeginning.getTimeInMillis());
		Timestamp to = new Timestamp(monthEnd.getTimeInMillis());
		
		TransactionList tl = new TransactionList();
		tl.setBalance(this.transactionService.getBalance(accountId));
		List<Transaction> transactions = this.transactionService.getTransactionsForAccount(accountId, from, to);
		int numTransactions = transactions.size();
		tl.setRunningBalances(new RunningBalance[numTransactions]);
		
		long balanceEnd = this.transactionService.getBalance(accountId, to);
		long balance = balanceEnd;
		Transaction t;
		
		for (int i = numTransactions - 1; i >= 0; i--) {
			t = transactions.get(i);
			
			if (t.isTransfer() && StringUtils.isBlank(t.getMemo())) {
				Transaction tt = this.transactionService.get(t.getTransferId());
				t.setMemo(String.format("(%s '%s')", t.getAmount() < 0 ? "To " : "From ", tt.getAccount().getName()));
			}
			
			tl.getRunningBalances()[numTransactions - i - 1] = new RunningBalance(t).setBalance(Util.formatPounds(balance));
			balance -= t.getAmount();			
		}
				
		tl.
			setAccount(a).
			setPeriodStart(from).
			setPeriodEnd(to).
			setPager(p);
		
		model.addAttribute("_tl", tl);
		model.addAttribute("_accounts", allAccounts);
		model.addAttribute("_accountId", accountId);
		
		return "transactionList";
	}

	@RequestMapping(value="/transaction/list/by/category/{categoryId}")	
	public String transactionListByCategoryNoPage(@PathVariable long categoryId, 
			HttpServletRequest req, ModelMap model) { 
		
		return transactionListByCategory(categoryId, 1, req, model);
	}
	
	@RequestMapping(value="/transaction/list/by/category/{categoryId}/all")	
	public String transactionListAllByCategoryNoPage(@PathVariable long categoryId, 
			HttpServletRequest req, ModelMap model) { 
		
		return transactionListAllByCategory(categoryId, 1, req, model);
	}
	
	@RequestMapping(value="/transaction/list/by/category/{categoryId}/{selectedPage}")	
	public String transactionListByCategory(@PathVariable long categoryId, 
			@PathVariable int selectedPage, HttpServletRequest req, ModelMap model) { 
		
		return getTransactionListByCategory(categoryId, selectedPage, 1000, req, model);
	}
	
	@RequestMapping(value="/transaction/list/by/category/{categoryId}/{selectedPage}/all")	
	public String transactionListAllByCategory(@PathVariable long categoryId, 
			@PathVariable int selectedPage, HttpServletRequest req, ModelMap model) { 
		
		return getTransactionListByCategory(categoryId, selectedPage, 0, req, model);
	}
	
	@SuppressWarnings("unchecked")
	private String getTransactionListByCategory(long categoryId, 
			int selectedPage, int limit, HttpServletRequest req, ModelMap model) {
		
		List<FlatTransaction> results = null;
		if (selectedPage == 1) {
			// Do a fresh search
			results = this.transactionService.getTransactionsForCategory(categoryId, limit);
			req.getSession().setAttribute(CATEGORY_SEARCH, results);
		}
		else {
			// Look for stored results
			results = (List<FlatTransaction>) req.getSession().getAttribute(CATEGORY_SEARCH);
			if (results == null) {
				results = this.transactionService.getTransactionsForCategory(categoryId, limit);
				req.getSession().setAttribute(CATEGORY_SEARCH, results);
			}
		}
		
		Pager<FlatTransaction> pager = new Pager<FlatTransaction>(results, 20, selectedPage);
		model.addAttribute("_pager", pager);
		model.addAttribute("_category", this.categoryService.get(categoryId));
		model.addAttribute("_limit", limit); 
		return "transactionListByCategory";
	}

	@RequestMapping(value="/transaction/list/by/payee/{payeeId}")	
	public String transactionListByPayeeNoPage(@PathVariable long payeeId, 
			HttpServletRequest req, ModelMap model) { 
		
		return transactionListByPayee(payeeId, 1, req, model);
	}
	
	@RequestMapping(value="/transaction/list/by/payee/{payeeId}/all")	
	public String transactionListAllByPayeeNoPage(@PathVariable long payeeId, 
			HttpServletRequest req, ModelMap model) { 
		
		return transactionListAllByPayee(payeeId, 1, req, model);
	}
	
	@RequestMapping(value="/transaction/list/by/payee/{payeeId}/{selectedPage}")	
	public String transactionListByPayee(@PathVariable long payeeId, 
			@PathVariable int selectedPage, HttpServletRequest req, ModelMap model) { 
		
		return getTransactionListByPayee(payeeId, selectedPage, 1000, req, model);
	}
	
	@RequestMapping(value="/transaction/list/by/payee/{payeeId}/{selectedPage}/all")	
	public String transactionListAllByPayee(@PathVariable long payeeId, 
			@PathVariable int selectedPage, HttpServletRequest req, ModelMap model) { 
		
		return getTransactionListByPayee(payeeId, selectedPage, 0, req, model);
	}
	
	@SuppressWarnings("unchecked")
	private String getTransactionListByPayee(long payeeId, 
			int selectedPage, int limit, HttpServletRequest req, ModelMap model) {
		
		List<FlatTransaction> results = null;
		if (selectedPage == 1) {
			// Do a fresh search
			results = this.transactionService.getTransactionsForPayee(payeeId, limit);
			req.getSession().setAttribute(PAYEE_SEARCH, results);
		}
		else {
			// Look for stored results
			results = (List<FlatTransaction>) req.getSession().getAttribute(PAYEE_SEARCH);
			if (results == null) {
				results = this.transactionService.getTransactionsForPayee(payeeId, limit);
				req.getSession().setAttribute(PAYEE_SEARCH, results);
			}
		}
		
		Pager<FlatTransaction> pager = new Pager<FlatTransaction>(results, 20, selectedPage);
		model.addAttribute("_pager", pager);
		model.addAttribute("_payee", this.payeeService.get(payeeId));
		model.addAttribute("_limit", limit); 
		return "transactionListByPayee";
	}
}
