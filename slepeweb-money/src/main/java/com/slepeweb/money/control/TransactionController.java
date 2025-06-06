package com.slepeweb.money.control;

import java.sql.Timestamp;
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

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Category_Group;
import com.slepeweb.money.bean.Category_GroupSet;
import com.slepeweb.money.bean.MonthPager;
import com.slepeweb.money.bean.NormalisedMonth;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.RunningBalance;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.TransactionList;
import com.slepeweb.money.bean.Transfer;
import com.slepeweb.money.component.FormSupport;
import com.slepeweb.money.component.TransactionFormSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value="/transaction")
public class TransactionController extends BaseController {
	
	public static final String TRANSACTION_FORM = "transactionForm";
	public static final String TRANSACTION_LIST = "transactionList";

	private static Logger LOG = Logger.getLogger(TransactionController.class);
	
	@Autowired private FormSupport formSupport;
	@Autowired private TransactionFormSupport transactionFormSupport;

	
	@RequestMapping(value="/list", method=RequestMethod.GET)	
	public String listNoAccount(HttpServletRequest req, HttpServletResponse res, ModelMap model) { 
		
		Account a = getHistory(req).getLastAccount();
		
		if (a.getId() > 0) {
			return listNoMonth(a.getId(), req, res, model);
		}
		
		return listNoMonth(Account.getDefault(), req, res, model);
	}
	
	@RequestMapping(value="/list/{accountId}")	
	public String listNoMonth(@PathVariable long accountId, HttpServletRequest req, HttpServletResponse res, ModelMap model) { 
		
		getHistory(req).getLastAccount().setId(accountId);
		Timestamp end = getHistory(req).getLastTransaction().getEntered();
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
		model.addAttribute("_yearSelector", this.transactionFormSupport.buildMonthSelector(pager));
		
		return TRANSACTION_LIST;
	}
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String addForm(HttpServletRequest req, ModelMap model) {
		this.transactionFormSupport.populateForm(model, new Transaction(), "add");		
		return TRANSACTION_FORM;
	}
	
	@RequestMapping(value="/add/{accountId}", method=RequestMethod.GET)
	public String addFormForAccount(@PathVariable long accountId, HttpServletRequest req, ModelMap model) {		
		Transaction t = new Transaction();
		Transaction last = getHistory(req).getLastTransaction();
		Account a = this.accountService.get(accountId);
		t.setAccount(a).setEntered(last.getEntered());		
		this.transactionFormSupport.populateForm(model, t, "add");		
		return TRANSACTION_FORM;
	}
	
	@RequestMapping(value="/form/{transactionId}", method=RequestMethod.GET)
	public String updateForm(@PathVariable long transactionId, HttpServletRequest req, ModelMap model) {
		Transaction t = this.transactionService.get(transactionId);
		if (t.isTransfer()) {
			Transaction mirror = this.transactionService.get(t.getTransferId());
			t.setPartReconciled(t.isReconciled() || mirror.isReconciled());
		}
		else {
			t.setPartReconciled(t.isReconciled());
		}
		
		getHistory(req).setLastTransaction(t);
		this.transactionFormSupport.populateForm(model, t, "update");
		return TRANSACTION_FORM;
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
			Payee p = identifyPayee(req.getParameter("payee"));		
			t = new Transaction().setPayee(p);
			
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
		
		if (! isUpdateMode) {
			t.setSource(3);
		}
		
		// Note: Transfers can NOT have split transactions
		if (isSplit) {
			List<String> allMajors = this.categoryService.getAllMajorValues();
			Category_GroupSet cgs = new Category_GroupSet("Splits", FormSupport.TRANSACTION_CTX, allMajors);
			Category_Group cg = this.formSupport.readCategoryInputs(req, 1, cgs);
			t.setSplits(cg.toSplitTransactions(this.categoryService, multiplier));
		}
		
		t = save(t);
		
		if (t != null) {
			getHistory(req).setLastTransaction(t);
			flash = String.format("success|Transaction successfully %s", isUpdateMode ? "updated" : "added");
			//this.cookieService.updateLastEnteredCookie(t.getEntered(), req, res);
		
			return new RedirectView(String.format("%s/transaction/form/%d?flash=%s", 
				req.getContextPath(), t.getId(), Util.encodeUrl(flash)));
		}
		else {
			flash = "failure|Failed to save transaction";
		}
	
		return new RedirectView(String.format("%s/transaction/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}
	
	private Payee identifyPayee(String name) {
		Payee p = this.payeeService.get(name);
		if (p.isNoPayee()) {
			p = new Payee().setName(name);
			try {
				p = this.payeeService.save(p);
				LOG.info(String.format("Created new payee: %s", name));
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to create new Payee");
			}
		}
		
		return p;
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
		Transaction t = this.transactionService.get(transactionId).
				setId(0L).
				setOrigId(0L).
				setEntered(Util.now()).
				setReconciled(false);
		
		this.transactionFormSupport.populateForm(model, t, "add");
		
		return TRANSACTION_FORM;
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