package com.slepeweb.money.control;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.MonthPager;
import com.slepeweb.money.bean.NormalisedMonth;
import com.slepeweb.money.bean.Option;
import com.slepeweb.money.bean.RunningBalance;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.TransactionList;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.SolrService;
import com.slepeweb.money.service.TransactionService;

@Controller
@RequestMapping(value="/transaction")
public class TransactionController extends BaseController {
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private TransactionService transactionService;
	@Autowired private SolrService solrService;
	
	@RequestMapping(value="/list")	
	public String listNoAccount(ModelMap model) { 
		List<Account> allAccounts = this.accountService.getAll(false);
		if (allAccounts.size() > 0) {
			return listNoMonth(allAccounts.get(0).getId(), model);
		}
		return null;
	}
	
	@RequestMapping(value="/list/{accountId}")	
	public String listNoMonth(@PathVariable long accountId, ModelMap model) { 
		Timestamp end = this.transactionService.getTransactionDateForAccount(accountId, false);		
		NormalisedMonth endMonth = new NormalisedMonth(end);
		return list(accountId, endMonth.getIndex(), model);
	}
	
	@RequestMapping(value="/list/{accountId}/{selectedMonthIndex}")	
	public String list(@PathVariable long accountId, @PathVariable int selectedMonthIndex, ModelMap model) {
		
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
		
		MonthPager pager = new MonthPager(firstMonth, selectedMonth, lastMonth);
		
		tl.
			setAccount(a).
			setPeriodStart(from).
			setPeriodEnd(to).
			setPager(pager);
		
		model.addAttribute("_tl", tl);
		model.addAttribute("_accounts", allAccounts);
		model.addAttribute("_accountId", accountId);
		model.addAttribute("_yearSelector", buildMonthSelector(pager));
		
		return "transactionList";
	}
	
	private List<Option> buildMonthSelector(MonthPager pager) {
		
		List<Option> yearSelector = new ArrayList<Option>();
		int numYears = pager.getFirstMonth().distanceBefore(pager.getLastMonth()) / 12;
		Calendar today = Util.today();
		int thisYear = today.get(Calendar.YEAR);
		NormalisedMonth m = new NormalisedMonth(today.getTime());
		Option o;
				
		for (int i = 0; i <= numYears; i++) {
			o = new Option(m.getIndex(), String.valueOf(thisYear - i));
			yearSelector.add(o);
			o.setSelected(m.getYear() == pager.getSelectedMonth().getYear());
			m.decrement(12);
		}
		
		return yearSelector;
	}

	@RequestMapping(value="/list/by/category/{categoryId}")	
	public String listByCategoryNoPage(@PathVariable long categoryId, 
			HttpServletRequest req, ModelMap model) { 
		
		return listByCategory(categoryId, 1, req, model);
	}
	
	@RequestMapping(value="/list/by/category/{categoryId}/all")	
	public String listAllByCategoryNoPage(@PathVariable long categoryId, 
			HttpServletRequest req, ModelMap model) { 
		
		return listAllByCategory(categoryId, 1, req, model);
	}
	
	@RequestMapping(value="/list/by/category/{categoryId}/{selectedPage}")	
	public String listByCategory(@PathVariable long categoryId, 
			@PathVariable int selectedPage, HttpServletRequest req, ModelMap model) { 
		
		return getListByCategory(categoryId, selectedPage, 1000, req, model);
	}
	
	@RequestMapping(value="/list/by/category/{categoryId}/{selectedPage}/all")	
	public String listAllByCategory(@PathVariable long categoryId, 
			@PathVariable int selectedPage, HttpServletRequest req, ModelMap model) { 
		
		return getListByCategory(categoryId, selectedPage, 0, req, model);
	}
	
	private String getListByCategory(long categoryId, 
			int selectedPage, int limit, HttpServletRequest req, ModelMap model) {
		
		SolrParams params = new SolrParams(new SolrConfig()).setCategoryId(categoryId).setPageNum(selectedPage);
		model.addAttribute("_response", this.solrService.query(params));
		model.addAttribute("_category", this.categoryService.get(categoryId));
		model.addAttribute("_limit", limit); 
		
		return "transactionListByCategory";
	}

	@RequestMapping(value="/list/by/payee/{payeeId}")	
	public String listByPayeeNoPage(@PathVariable long payeeId, 
			HttpServletRequest req, ModelMap model) { 
		
		return listByPayee(payeeId, 1, req, model);
	}
	
	@RequestMapping(value="/list/by/payee/{payeeId}/all")	
	public String listAllByPayeeNoPage(@PathVariable long payeeId, 
			HttpServletRequest req, ModelMap model) { 
		
		return listAllByPayee(payeeId, 1, req, model);
	}
	
	@RequestMapping(value="/list/by/payee/{payeeId}/{selectedPage}")	
	public String listByPayee(@PathVariable long payeeId, 
			@PathVariable int selectedPage, HttpServletRequest req, ModelMap model) { 
		
		return getListByPayee(payeeId, selectedPage, 1000, req, model);
	}
	
	@RequestMapping(value="/list/by/payee/{payeeId}/{selectedPage}/all")	
	public String listAllByPayee(@PathVariable long payeeId, 
			@PathVariable int selectedPage, HttpServletRequest req, ModelMap model) { 
		
		return getListByPayee(payeeId, selectedPage, 0, req, model);
	}
	
	private String getListByPayee(long payeeId, 
			int selectedPage, int limit, HttpServletRequest req, ModelMap model) {
		
		SolrParams params = new SolrParams(new SolrConfig()).setPayeeId(payeeId).setPageNum(selectedPage);
		model.addAttribute("_response", this.solrService.query(params));
		model.addAttribute("_payee", this.payeeService.get(payeeId));
		model.addAttribute("_limit", limit); 
		
		return "transactionListByPayee";
	}	
}