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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.MonthPager;
import com.slepeweb.money.bean.NormalisedMonth;
import com.slepeweb.money.bean.Option;
import com.slepeweb.money.bean.Payee;
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

	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String addForm(ModelMap model) {
		
		model.addAttribute("_transaction", new Transaction());
		model.addAttribute("_formMode", "add");
		model.addAttribute("_allAccounts", this.accountService.getAll(false));
		model.addAttribute("_allPayees", this.payeeService.getAll());
		model.addAttribute("_allMajorCategories", this.categoryService.getAllMajorValues());		
		return "transactionForm";
	}
	
	@RequestMapping(value="/form/{transactionId}", method=RequestMethod.GET)
	public String updateForm(@PathVariable long transactionId, ModelMap model) {
		
		Transaction t = this.transactionService.get(transactionId);
		model.addAttribute("_transaction", t);
		model.addAttribute("_formMode", "update");
		model.addAttribute("_allAccounts", this.accountService.getAll(false));
		model.addAttribute("_allPayees", this.payeeService.getAll());
		model.addAttribute("_allMajorCategories", this.categoryService.getAllMajorValues());
		model.addAttribute("_allMinorCategories", this.categoryService.getAllMinorValues(t.getCategory().getMajor()));
		model.addAttribute("_numDeletableTransactions", 0);
		return "transactionForm";
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public RedirectView update(HttpServletRequest req, ModelMap model) {
		
		String flash;	
		boolean isUpdateMode = req.getParameter("formMode").equals("update");
		
		Account a = this.accountService.get(Long.valueOf(req.getParameter("account")));
		Payee p = this.payeeService.get(Long.valueOf(req.getParameter("payee")));
		Category c = this.categoryService.get(req.getParameter("major"), req.getParameter("minor"));
		Transaction t = 
				new Transaction().
				setId(Long.valueOf(req.getParameter("id")));
		
		if (a != null && p != null && c != null) {
			t.
				setAccount(a).
				setPayee(p).
				setCategory(c).
				setEntered(Util.parseTimestamp(req.getParameter("entered"))).
				setMemo(req.getParameter("memo")).
				setAmount(Util.parsePounds(req.getParameter("amount")));
			
			try {
				this.transactionService.save(t);
				flash = String.format("success|Transaction successfully %s", isUpdateMode ? "updated" : "added");
			}
			catch (Exception e) {
				flash = String.format("failure|Missing key data [%s]", e.getMessage());
			}
		}
		else {
			flash = String.format("failure|Failed to %s transaction", isUpdateMode ? "update" : "add new");
		}
	
		return new RedirectView(String.format("%s/transaction/form/%d?flash=%s", 
				req.getContextPath(), t.getId(), Util.encodeUrl(flash)));
	}
	
	@RequestMapping(value="/delete/{transactionId}", method=RequestMethod.GET)
	public RedirectView delete(@PathVariable long transactionId, HttpServletRequest req, ModelMap model) {
		
		String flash;
		Transaction t = this.transactionService.get(transactionId);
		
		try {
			this.transactionService.delete(transactionId);
			flash="success|Transaction successfully deleted";
		}
		catch (Exception e) {
			flash="failure|Failed to delete transaction";
		}
		
		return new RedirectView(String.format("%s/transaction/list/%d?flash=%s", 
				req.getContextPath(), t.getAccount().getId(), Util.encodeUrl(flash)));
	}	
}