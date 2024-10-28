package com.slepeweb.money.control;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.ScheduledTransaction;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.SplitTransactionFormComponent;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/schedule")
public class ScheduledTransactionController extends BaseController {
	
	private void populateForm(ModelMap model, ScheduledTransaction t, String mode) {	
		
		List<Account> allAccounts = this.accountService.getAll(false);
		List<String> allMajors = this.categoryService.getAllMajorValues();

		model.addAttribute("_schedule", t);
		model.addAttribute("_daysOfMonth", getDaysOfMonth());
		model.addAttribute("_formMode", mode);
		model.addAttribute("_allAccounts", allAccounts);
		model.addAttribute("_allPayees", this.payeeService.getAll());		
		model.addAttribute("_allMajorCategories",allMajors );		
		model.addAttribute("_allMinorCategories", this.categoryService.getAllMinorValues(t.getCategory().getMajor()));
		
		List<SplitTransactionFormComponent> arr = new ArrayList<SplitTransactionFormComponent>();
		SplitTransactionFormComponent fc;
		
		for (SplitTransaction st : t.getSplits()) {
			fc = new SplitTransactionFormComponent(st).
					setAllMajors(allMajors).
					setAllMinors(this.categoryService.getAllMinorValues(st.getCategory().getMajor()));
			arr.add(fc);
		}
		
		int len = arr.size();
		boolean extended = false;
		
		// Do we need to pad out the list?
		if (len < 3) {
			for (int i = len; i < 3; i++) {
				arr.add(new SplitTransactionFormComponent().setAllMajors(allMajors));
				extended = true;
			}
		}
		
		// If we didn'd pad out, that means we had 3 or more splits. In this case, provide 1 empty slot.
		if (! extended) {
			arr.add(new SplitTransactionFormComponent().setAllMajors(allMajors));
		}
		
		model.addAttribute("_allSplits", arr);
	}
	
	@RequestMapping(value="/list")	
	public String list(ModelMap model) { 
		model.addAttribute("_scheduled", this.scheduledTransactionService.getAll());
		return "scheduleList";
	}
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String addForm(ModelMap model) {
		ScheduledTransaction scht = new ScheduledTransaction();
		scht.
				setAccount(new Account().setName("")).
				setPayee(new Payee().setName("")).
				setCategory(new Category().setMajor("").setMinor(""));
		
		populateForm(model, scht, "add");
		return "scheduleForm";
	}
	
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String updateForm(@PathVariable long id, ModelMap model) {		
		populateForm(model, this.scheduledTransactionService.get(id), "update");
		return "scheduleForm";
	}
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public RedirectView save(HttpServletRequest req, ModelMap model) {
		
		boolean isSplit = req.getParameter("paymenttype").equals("split");
		long multiplier = req.getParameter("debitorcredit").equals("debit") ? -1L : 1L;

		String flash;	
		boolean isUpdateMode = req.getParameter("formMode").equals("update");
		Account a = this.accountService.get(Long.valueOf(req.getParameter("account")));
		
		Account m = null;
		String mirror = req.getParameter("xferaccount");
		if (StringUtils.isNotBlank(mirror)) {
			m = this.accountService.get(Long.valueOf(mirror));
		}
		
		Payee p = this.payeeService.get(req.getParameter("payee"));
		if (p == null) {
			p = this.payeeService.get("");
		}
		
		Category c = this.categoryService.get(req.getParameter("major"), req.getParameter("minor"));
		if (c == null) {
			c = this.categoryService.get("", "");
		}
		
		ScheduledTransaction scht = new ScheduledTransaction().
				setLabel(req.getParameter("label")).
				setDay(Integer.parseInt(req.getParameter("day"))).
				setMirror(m);

		scht.
				setAccount(a).
				setPayee(p).
				setCategory(c).
				setAmount(Util.parsePounds(req.getParameter("amount")) * multiplier).
				setMemo(req.getParameter("memo")).
				setReference(req.getParameter("reference")).
				setSplit(isSplit);
		
		if (isUpdateMode) {
			scht.setId(Long.valueOf(req.getParameter("id")));
		}
		else {
			// Set the 'date last entered' field to some arbirary date a long time ago,
			// BUT NOT ZERO, since this is used as the test for an unset field.
			scht.setEntered(new Timestamp(32000L));
		}
		
		// Note: Transfers can NOT have split transactions
		if (isSplit) {
			int index = 1;
			SplitTransaction st;
			Category sc;
			
			do {
				sc = this.categoryService.get(
						req.getParameter("major_" + index), 
						req.getParameter("minor_" + index));
				
				st = new SplitTransaction().
					setTransactionId(scht.getId()).
					setCategory(sc).
					setMemo(req.getParameter("memo_" + index)).
					setAmount(Util.parsePounds(req.getParameter("amount_" + index)) * multiplier);
				
				// The transactionId for each ScheduledSplit will be assigned within TransactionService.save(t).
				if (st.isPopulated()) {
					scht.getSplits().add(st);
					index++;
				}
				else {
					index = -1;
				}
			}
			while (index > 0);
		}
		
		
		try {
			scht = this.scheduledTransactionService.save(scht);
			flash = String.format("success|Scheduled transaction successfully %s", isUpdateMode ? "updated" : "added");
		}
		catch (Exception e) {
			flash = String.format("failure|Failed to %s scheduled transaction", isUpdateMode ? "update" : "add new");
		}
	
		return new RedirectView(String.format("%s/schedule/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public RedirectView delete(@PathVariable long id, HttpServletRequest req, ModelMap model) {
		
		String flash;		
		try {
			this.scheduledTransactionService.delete(id);
			flash="success|Scheduled transaction successfully deleted";
		}
		catch (Exception e) {
			flash="failure|Failed to delete scheduled transaction";
		}
		
		return new RedirectView(String.format("%s/schedule/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}	
	
	private List<Integer> getDaysOfMonth() {
		List<Integer> list = new ArrayList<Integer>(28);
		for (int i = 1; i <= 28; i++) {
			list.add(i);
		}
		return list;
	}
}