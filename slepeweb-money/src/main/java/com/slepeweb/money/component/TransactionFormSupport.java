package com.slepeweb.money.component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Category_Group;
import com.slepeweb.money.bean.Category_GroupSet;
import com.slepeweb.money.bean.MonthPager;
import com.slepeweb.money.bean.NormalisedMonth;
import com.slepeweb.money.bean.Option;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.Transfer;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class TransactionFormSupport {
	
	@Autowired FormSupport formSupport;
	@Autowired private CategoryService categoryService;
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;

	public List<SplitTransaction> readCategoryInputs(HttpServletRequest req, long amountPlusOrMinus) {
		int index = 1;
		SplitTransaction st;
		Category c;
		String major, minor;
		List<SplitTransaction> list = new ArrayList<SplitTransaction>();
		int numSplits = Integer.valueOf(req.getParameter("numsplits"));
		
		do {
			major = req.getParameter("major_" + index);
			
			if (StringUtils.isNotBlank(major)) {			
				minor = req.getParameter("minor_" + index);
				c = this.categoryService.get(major, minor);
				
				st = new SplitTransaction().
					setCategory(c).
					setMemo(req.getParameter("memo_" + index)).
					setAmount(Util.parsePounds(req.getParameter("amount_" + index)) * amountPlusOrMinus);
				
				list.add(st);
			}
			
			index++;
		}
		while (index <= numSplits);
		
		return list;
	}
	
	public void populateForm(ModelMap model, Transaction t, String mode) {	
		
		List<Account> allAccounts = this.accountService.getAll(false);
		
		// Not sure about this logic
		/*
		if (! allAccounts.contains(t.getAccount())) {
			allAccounts.add(t.getAccount());
		}
		*/
		
		model.addAttribute("_transaction", t);
		populateForm(model, t, allAccounts, mode);
				
		if (t.isTransfer()) {
			Transfer tt = (Transfer) t;
			model.addAttribute("_xferAccount", tt.getMirrorAccount());
			if (! allAccounts.contains(tt.getMirrorAccount())) {
				allAccounts.add(tt.getMirrorAccount());
			}
		}
	}
	
	public void populateForm(ModelMap model, Transaction t, List<Account> allAccounts, String mode) {
		List<String> allMajors = this.categoryService.getAllMajorValues();

		model.addAttribute("_formMode", mode);
		model.addAttribute("_allAccounts", allAccounts);
		model.addAttribute("_allPayees", this.payeeService.getAll());
		model.addAttribute("_allMajorCategories", allMajors);
		
		if (t.getCategory() != null) {
			model.addAttribute("_allMinorCategories", this.categoryService.getAllMinorValues(t.getCategory().getMajor()));
		}
		
		Category_GroupSet cgs = new Category_GroupSet("Transaction Splits", FormSupport.TRANSACTION_CTX, allMajors);
		Category_Group cg = this.formSupport.populateCategory_Group(1, "Splits", t.getSplits(), SplitTransaction.class);
		cg.setVisible(true).setLastVisible(true);
		cgs.addGroup(cg);
		
		model.addAttribute("_transactionSplits", cgs);
	}
	
	public List<Option> buildMonthSelector(MonthPager pager) {
		
		List<Option> yearSelector = new ArrayList<Option>();
		int numYears = pager.getFirstMonth().distanceBefore(pager.getLastMonth()) / 12;
		Calendar today = Util.today();
		int thisYear = today.get(Calendar.YEAR);
		NormalisedMonth m = new NormalisedMonth(today.getTime());
		Option o;
				
		for (int i = 0; i <= numYears + 1; i++) {
			o = new Option(m.getIndex(), String.valueOf(thisYear - i));
			yearSelector.add(o);
			o.setSelected(m.getYear() == pager.getSelectedMonth().getYear());
			m.decrement(12);
		}
		
		return yearSelector;
	}
}
