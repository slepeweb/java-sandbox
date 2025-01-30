package com.slepeweb.money.control;

import java.sql.Timestamp;
import java.util.List;

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
import com.slepeweb.money.bean.Category_Group;
import com.slepeweb.money.bean.Category_GroupSet;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.ScheduledTransaction;
import com.slepeweb.money.component.FormSupport;
import com.slepeweb.money.component.TransactionFormSupport;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/schedule")
public class ScheduledTransactionController extends BaseController {
	
	@Autowired private FormSupport formSupport;
	@Autowired private TransactionFormSupport transactionFormSupport;
	
	private void populateForm(ModelMap model, ScheduledTransaction t, String mode) {			
		List<Account> allAccounts = this.accountService.getAll(false);
		model.addAttribute("_schedule", t);
		model.addAttribute("_daysOfMonth", this.formSupport.getDaysOfMonth());
		this.transactionFormSupport.populateForm(model, t, allAccounts, mode);
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
		
		// Payment type is set by radio input, and so can only have one value
		String paymentType = req.getParameter("paymenttype");
		boolean isSplit = paymentType.equals("split");
		boolean isTransfer = paymentType.equals("transfer");
		boolean isStandard = ! (isSplit || isTransfer);
		
		long multiplier = req.getParameter("debitorcredit").equals("debit") ? -1L : 1L;

		String flash;	
		boolean isUpdateMode = req.getParameter("formMode").equals("update");
		Account a = this.accountService.get(Long.valueOf(req.getParameter("account")));
		
		Account m = null;
		String mirror = req.getParameter("xferaccount");
		if (isTransfer && StringUtils.isNotBlank(mirror)) {
			m = this.accountService.get(Long.valueOf(mirror));
		}
		
		Payee p = this.payeeService.get(req.getParameter("payee"));
		if (p == null) {
			p = this.payeeService.get("");
		}
		
		Category c = null;
		if (isStandard) {
			c = this.categoryService.get(req.getParameter("major"), req.getParameter("minor"));
		}

		if (c == null) {
			c = this.categoryService.get("", "");
		}

		ScheduledTransaction scht = new ScheduledTransaction().
				setLabel(req.getParameter("label")).
				setNextDate(Util.parseTimestamp(req.getParameter("nextdate"))).
				setPeriod(req.getParameter("period")).
				setMirror(m).
				setEnabled(StringUtils.isNotBlank(req.getParameter("enabled")));

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
			List<String> allMajors = this.categoryService.getAllMajorValues();
			Category_GroupSet cgs = new Category_GroupSet("Splits", FormSupport.TRANSACTION_CTX, allMajors);
			Category_Group cg = this.formSupport.readCategoryInputs(req, 1, cgs);
			scht.setSplits(cg.toSplitTransactions(this.categoryService, multiplier));
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
	
}