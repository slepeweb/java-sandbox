package com.slepeweb.money.control;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.NormalisedMonth;
import com.slepeweb.money.bean.Pager;
import com.slepeweb.money.bean.RunningBalance;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.TransactionList;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.TransactionService;
import com.slepeweb.money.service.Util;

@Controller
public class PageController extends BaseController {
	
	@Autowired private AccountService accountService;
	@Autowired private TransactionService transactionService;
	
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
			
		Pager p = new Pager(firstMonth, selectedMonth, lastMonth);
		
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
				t.setMemo(String.format("(%s account '%s')", t.getAmount() < 0 ? "To " : "From ", tt.getAccount().getName()));
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
		
		return "home";
	}

}
