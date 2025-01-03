package com.slepeweb.money.control;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.MonthPager;
import com.slepeweb.money.bean.MultiSplitCounter;
import com.slepeweb.money.bean.NormalisedMonth;
import com.slepeweb.money.bean.Option;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.RunningBalance;
import com.slepeweb.money.bean.SplitInput;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.SplitTransactionFormComponent;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.TransactionList;
import com.slepeweb.money.bean.Transfer;
import com.slepeweb.money.service.CookieService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/*
 * TODO: Things to look at:
 * - Need to store the last selected account on the transaction list page, so that
 *   the list menu can refer to it
 * - No way to remove a category from split transactions
 * - If you have 3 splits, then blank out no. 2, then only the first is saved.
 * - Fourth split on form breaks update
 * - Provide option to ignore split errors
 */


@Controller
@RequestMapping(value="/transaction")
public class TransactionController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(TransactionController.class);
	
	@Autowired private CookieService cookieService;
	
	@RequestMapping(value="/list", method=RequestMethod.GET)	
	public String listNoAccount(HttpServletRequest req, HttpServletResponse res, ModelMap model) { 
		Long accountId = this.cookieService.getAccountId(req);
		if (accountId != null) {
			return listNoMonth(accountId.longValue(), req, res, model);
		}
		
		List<Account> allAccounts = this.accountService.getAll(false);
		if (allAccounts.size() > 0) {
			return listNoMonth(allAccounts.get(0).getId(), req, res, model);
		}
		
		return null;
	}
	
	@RequestMapping(value="/list/{accountId}")	
	public String listNoMonth(@PathVariable long accountId, HttpServletRequest req, HttpServletResponse res, ModelMap model) { 
		
		this.cookieService.updateAccountCookie(accountId, req, res);
		
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
		
		if (a != null && a.isClosed()) {
			allAccounts.add(a);
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
			tl.getRunningBalances()[numTransactions - i - 1] = new RunningBalance(t).setBalance(balance);
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

	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String addForm(ModelMap model) {		
		populateForm(model, new Transaction(), "add");
		return "transactionForm";
	}
	
	@RequestMapping(value="/add/{accountId}", method=RequestMethod.GET)
	public String addFormForAccount(@PathVariable long accountId, HttpServletRequest req, ModelMap model) {
		
		Timestamp lastEntered = this.cookieService.getLastEntered(req);
		
		populateForm(model, 
				new Transaction().
					setAccount(this.accountService.get(accountId)).
					setEntered(lastEntered != null ? lastEntered : Util.now()), 
				"add");
		
		return "transactionForm";
	}
	
	@RequestMapping(value="/form/{transactionId}", method=RequestMethod.GET)
	public String updateForm(@PathVariable long transactionId, ModelMap model) {
		populateForm(model, this.transactionService.get(transactionId), "update");
		return "transactionForm";
	}
	
	private void populateForm(ModelMap model, Transaction t, String mode) {	
		
		List<Account> allAccounts = this.accountService.getAll(false);
		if (! allAccounts.contains(t.getAccount())) {
			allAccounts.add(t.getAccount());
		}
		
		model.addAttribute("_transaction", t);
		model.addAttribute("_formMode", mode);
		model.addAttribute("_allAccounts", allAccounts);
		model.addAttribute("_allPayees", this.payeeService.getAll());
		model.addAttribute("_numDeletableTransactions", 0);
		
		
		List<String> allMajors = this.categoryService.getAllMajorValues();
		model.addAttribute("_allMajorCategories", allMajors);
		
		if (t.getCategory() != null) {
			model.addAttribute("_allMinorCategories", this.categoryService.getAllMinorValues(t.getCategory().getMajor()));
		}
		
		List<SplitTransactionFormComponent> arr = new ArrayList<SplitTransactionFormComponent>();
		SplitTransactionFormComponent c;
		
		for (SplitTransaction st : t.getSplits()) {
			c = new SplitTransactionFormComponent(st).
					setAllMajors(allMajors).
					setAllMinors(this.categoryService.getAllMinorValues(st.getCategory().getMajor()));
			arr.add(c);
		}
		
		model.addAttribute("_allSplits", arr);
		
		if (t.isTransfer()) {
			Transfer tt = (Transfer) t;
			model.addAttribute("_xferAccount", tt.getMirrorAccount());
			if (! allAccounts.contains(tt.getMirrorAccount())) {
				allAccounts.add(tt.getMirrorAccount());
			}
		}
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public RedirectView update(HttpServletRequest req, HttpServletResponse res, ModelMap model) {
		
		String flash;	
		boolean isUpdateMode = req.getParameter("formMode").equals("update");
		boolean isTransfer = req.getParameter("paymenttype").equals("transfer");
		long mirrorAccountId = Util.toLong(req.getParameter("xferaccount"));
		boolean isSplit = req.getParameter("paymenttype").equals("split");
		long multiplier = req.getParameter("debitorcredit").equals("debit") ? -1L : 1L;
		
		Account a = this.accountService.get(Util.toLong(req.getParameter("account")));
		Payee noPayee = this.payeeService.getNoPayee();
		Category noCategory = this.categoryService.getNoCategory();
		Category c;
		Transaction t;
		
		if (isTransfer) {
			// Override setting on form - 'split' has no meaning with transfers
			isSplit = false;
			
			t = new Transfer().setMirrorAccount(this.accountService.get(mirrorAccountId));
			t.
				setPayee(noPayee).
				setCategory(noCategory);
		}
		else {
			t = new Transaction().
				setPayee(this.payeeService.get(req.getParameter("payee")));
			
			if (isSplit) {
				t.setCategory(noCategory);
			}
			else {
				c = this.categoryService.get(req.getParameter("major"), req.getParameter("minor"));
				t.setCategory(c == null ? noCategory : c);
			}
		}
		
		t.		
			setId(Util.toLong(req.getParameter("id"))).
			setOrigId(Util.toLong(req.getParameter("origid"))).
			setAccount(a).
			setEntered(Util.parseTimestamp(req.getParameter("entered"))).
			setMemo(req.getParameter("memo")).
			setAmount(Util.parsePounds(req.getParameter("amount")) * multiplier).
			setSplit(isSplit);
		
		// Note: Transfers can NOT have split transactions
		if (isSplit) {
			String countersJson = req.getParameter("counterStore");
			MultiSplitCounter counters = fromJson(new TypeReference<MultiSplitCounter>() {}, countersJson);
			SplitTransaction st;
			
			for (SplitInput in : readMultiSplitInput(req, counters)) {
				st = new SplitTransaction().
					setTransactionId(t.getId()).
					setCategory(this.categoryService.get(in.getMajor(), in.getMinor())).
					setMemo(in.getMemo()).
					// TODO: need to avoid negative amounts on form - use <select> for debit/credit
					setAmount(t.isDebit() ? -in.getAmount() : in.getAmount());
				
				// The transactionId for each SplitTransaction will be assigned within TransactionService.save(t).
				if (st.isPopulated()) {
					t.getSplits().add(st);
				}
			}
		}
		
		if (save(t) != null) {
			flash = String.format("success|Transaction successfully %s", isUpdateMode ? "updated" : "added");
			this.cookieService.updateLastEnteredCookie(t.getEntered(), req, res);
		
			return new RedirectView(String.format("%s/transaction/form/%d?flash=%s", 
				req.getContextPath(), t.getId(), Util.encodeUrl(flash)));
		}
		else {
			flash = "failure|Failed to save transaction";
		}
	
		return new RedirectView(String.format("%s/transaction/list/%d?flash=%s", 
				req.getContextPath(), t.getAccount().getId(), Util.encodeUrl(flash)));
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
	
	@RequestMapping(value="/copy/{transactionId}", method=RequestMethod.GET)
	public String copy(@PathVariable long transactionId, HttpServletRequest req, ModelMap model) {
		populateForm(model, this.transactionService.get(transactionId).
				setId(0L).setOrigId(0L).setEntered(Util.now()), "add");
		return "transactionForm";
	}	
	
	private Transaction save(Transaction t) {
		try {
			return this.transactionService.save(t);
		}
		catch (Exception e) {
			LOG.error("Failed to save transaction", e);
			return null;
		}
	}	
}