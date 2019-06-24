package com.slepeweb.money.control;

import java.util.ArrayList;
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
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.ScheduledSplitBak;
import com.slepeweb.money.bean.ScheduledTransaction;
import com.slepeweb.money.bean.SplitTransactionFormComponent;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.ScheduledSplitService;
import com.slepeweb.money.service.ScheduledTransactionService;

@Controller
@RequestMapping(value="/schedule")
public class ScheduledTransactionController extends BaseController {
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private ScheduledTransactionService scheduledTransactionService;
	@Autowired private ScheduledSplitService scheduledSplitService;
	
	private void populateForm(ModelMap model, ScheduledTransaction t, String mode) {	
		
		Account a = this.accountService.get(t.getAccountId());
		Account m = null;
		if (t.getMirrorId() > 0) {
			m = this.accountService.get(t.getMirrorId());
		}
		
		List<Account> allAccounts = this.accountService.getAll(false);

		Payee p = this.payeeService.get(t.getPayee());
		Category c = this.categoryService.get(t.getMajorCategory(), t.getMinorCategory());
		
		model.addAttribute("_daysOfMonth", getDaysOfMonth());
		model.addAttribute("_scheduledTransaction", t);
		model.addAttribute("_account", a);
		model.addAttribute("_mirror", m);
		model.addAttribute("_payee", p);
		model.addAttribute("_category", c);
		model.addAttribute("_formMode", mode);
		model.addAttribute("_allAccounts", allAccounts);
		model.addAttribute("_allPayees", this.payeeService.getAll());
		
		List<String> allMajors = this.categoryService.getAllMajorValues();
		model.addAttribute("_allMajorCategories",allMajors );		
		model.addAttribute("_allMinorCategories", this.categoryService.getAllMinorValues(c.getMajor()));
		
		List<SplitTransactionFormComponent> arr = new ArrayList<SplitTransactionFormComponent>();
		SplitTransactionFormComponent fc;
		Category sc;
		
		for (ScheduledSplitBak st : this.scheduledSplitService.get(t.getId())) {
			sc = this.categoryService.get(st.getCategoryId());
			fc = new SplitTransactionFormComponent().
					setCategory(sc).
					setAllMajors(allMajors).
					setAllMinors(this.categoryService.getAllMinorValues(sc.getMajor())).
					setMemo(st.getMemo()).
					setAmount(st.getAmount());
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
		populateForm(model, new ScheduledTransaction(), "add");
		return "scheduleForm";
	}
	
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String updateForm(@PathVariable long id, ModelMap model) {		
		populateForm(model, this.scheduledTransactionService.get(id), "update");
		return "scheduleForm";
	}
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public RedirectView update(HttpServletRequest req, ModelMap model) {
		
		boolean isSplit = req.getParameter("paymenttype").equals("split");
		long multiplier = req.getParameter("debitorcredit").equals("debit") ? -1L : 1L;

		String flash;	
		boolean isUpdateMode = req.getParameter("formMode").equals("update");
		Account a = this.accountService.get(req.getParameter("account"));
		
		Account m = null;
		String mirror = req.getParameter("xferaccount");
		if (StringUtils.isNotBlank(mirror)) {
			m = this.accountService.get(mirror);
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
				setId(Long.valueOf(req.getParameter("id"))).
				setLabel(req.getParameter("label")).
				setDay(Integer.parseInt(req.getParameter("day"))).
				setAccountId(a.getId()).
				setMirrorId(m != null ? m.getId() : 0L).
				setPayeeId(p.getId()).
				setCategoryId(c.getId()).
				setAmount(Util.parsePounds(req.getParameter("amount"))).
				setMemo(req.getParameter("memo")).
				setReference(req.getParameter("reference")).
				setSplit(isSplit);
		
		// Note: Transfers can NOT have split transactions
		if (isSplit) {
			int index = 1;
			ScheduledSplitBak st;
			Category sc;
			
			do {
				sc = this.categoryService.get(
						req.getParameter("major_" + index), 
						req.getParameter("minor_" + index));
				
				st = new ScheduledSplitBak().
					setScheduledTransactionId(scht.getId()).
					setCategoryId(sc.getId()).
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
			this.scheduledTransactionService.save(scht);
			flash = String.format("success|Scheduled transaction successfully %s", isUpdateMode ? "updated" : "added");
		}
		catch (Exception e) {
			flash = String.format("failure|Failed to %s scheduled transaction", isUpdateMode ? "update" : "add new");
		}
	
		return new RedirectView(String.format("%s/schedule/edit/%d?flash=%s", 
				req.getContextPath(), scht.getId(), Util.encodeUrl(flash)));
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
	
	private long toLong(HttpServletRequest req, String s) {
		if (StringUtils.isNotBlank(s)) {
			return Util.toLong(req.getParameter(s));
		}
		return 0L;
		
	}
	
	private List<Integer> getDaysOfMonth() {
		List<Integer> list = new ArrayList<Integer>(28);
		for (int i = 1; i <= 28; i++) {
			list.add(i);
		}
		return list;
	}
}