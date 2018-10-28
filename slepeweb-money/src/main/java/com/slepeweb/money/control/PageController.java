package com.slepeweb.money.control;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.bean.Account;
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
	
	@RequestMapping(value="/list/{accountId}")	
	public RedirectView homepage(@PathVariable int accountId, HttpServletRequest req, ModelMap model) {
 
		List<Transaction> l = this.transactionService.getLatestTransactionForAccount(accountId);
		int monthOffset = 1;
		
		if (l.size() > 0) {
			Timestamp ts = l.get(0).getEntered();
			Calendar lastEntry = Util.today();
			lastEntry.setTime(ts);
			Calendar today = Util.today();
			monthOffset = 
					((today.get(Calendar.YEAR) - lastEntry.get(Calendar.YEAR)) * 12) +
					(today.get(Calendar.MONTH) - lastEntry.get(Calendar.MONTH)) +
					1;			
		}
		
		model.clear();
		return new RedirectView(String.format("%s/list/%d/%d", req.getContextPath(), accountId, monthOffset));
	}
	
	@RequestMapping(value="/list/{accountId}/{monthOffset}")	
	public String homepage(@PathVariable int accountId, @PathVariable int monthOffset, ModelMap model) {
		
		Account a = this.accountService.get(accountId);
		List<Account> allAccounts = this.accountService.getAll(false);
		
		if (a == null && allAccounts.size() > 0) {
			a = allAccounts.get(0);
		}
		
		Calendar monthEnd = Calendar.getInstance();
		Util.zeroTimeOfDay(monthEnd);
		monthEnd.add(Calendar.MONTH, -(monthOffset - 1));
		monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getMaximum(Calendar.DAY_OF_MONTH));
		
		Calendar monthBeginning = Calendar.getInstance();
		Util.zeroTimeOfDay(monthBeginning);
		monthBeginning.add(Calendar.MONTH, -(monthOffset - 1));
		monthBeginning.set(Calendar.DAY_OF_MONTH, 1);
		
		Timestamp from = new Timestamp(monthBeginning.getTimeInMillis());
		Timestamp to = new Timestamp(monthEnd.getTimeInMillis());
		
		TransactionList tl = new TransactionList();
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
			setPage(monthOffset);
		
		model.addAttribute("_tl", tl);
		model.addAttribute("_accounts", allAccounts);
		
		return "home";
	}

}
