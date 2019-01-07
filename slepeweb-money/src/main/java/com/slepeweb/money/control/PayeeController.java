package com.slepeweb.money.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.NamedList;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.TransactionService;

@Controller
@RequestMapping(value="/payee")
public class PayeeController extends BaseController {
	
	@Autowired private PayeeService payeeService;
	@Autowired private TransactionService transactionService;
	
	@RequestMapping(value="/list")	
	public String list(ModelMap model) { 
		List<NamedList<Payee>> payees = new ArrayList<NamedList<Payee>>();
		NamedList<Payee> mapping = null;
		String lastName = null, nextName = null;
		List<Payee> all = this.payeeService.getAll();
		
		for (Payee p : all) {
			if (p.getName().length() == 0) {
				// This happens, not sure why
				continue;
			}
			
			nextName = p.getName().substring(0, 1).toUpperCase();
			if (StringUtils.isNumeric(nextName)) {
				nextName = "0";
			}
			
			if (lastName == null || ! lastName.equals(nextName)) {
				mapping = new NamedList<Payee>(nextName, new ArrayList<Payee>());
				payees.add(mapping);
				lastName = nextName;
			}
			
			mapping.getObjects().add(p);
		}
		
		model.addAttribute("_payees", payees);
		model.addAttribute("_count", all.size());
		return "payeeList";
	}
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String addForm(ModelMap model) {
		
		model.addAttribute("_payee", new Payee());
		model.addAttribute("_formMode", "add");
		return "payeeForm";
	}
	
	@RequestMapping(value="/form/{payeeId}", method=RequestMethod.GET)
	public String updateForm(@PathVariable long payeeId, ModelMap model) {
		
		model.addAttribute("_payee", this.payeeService.get(payeeId));
		model.addAttribute("_formMode", "update");
		model.addAttribute("_numDeletableTransactions", this.transactionService.getNumTransactionsForPayee(payeeId));
		return "payeeForm";
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public RedirectView update(HttpServletRequest req, ModelMap model) {
		
		String flash;	
		boolean isUpdateMode = req.getParameter("formMode").equals("update");
		
		Payee p = new Payee().
				setId(Long.valueOf(req.getParameter("id"))).
				setName(req.getParameter("name"));
		
		try {
			this.payeeService.save(p);
			flash = String.format("success|Payee successfully %s", isUpdateMode ? "updated" : "added");
		}
		catch (Exception e) {
			flash = String.format("failure|Failed to %s payee", isUpdateMode ? "update" : "add new");
		}
	
		return new RedirectView(String.format("%s/payee/form/%d?flash=%s", 
				req.getContextPath(), p.getId(), Util.encodeUrl(flash)));
	}
	
	@RequestMapping(value="/delete/{payeeId}", method=RequestMethod.GET)
	public RedirectView delete(@PathVariable long payeeId, HttpServletRequest req, ModelMap model) {
		
		String flash;		
		User u = (User) model.get(USER);
		long numDeletables = this.transactionService.getNumTransactionsForPayee(payeeId);
		
		if ((u != null && u.getUsername().equals("MONEY_ADMIN")) || numDeletables == 0) {		
			try {
				this.payeeService.delete(payeeId);
				flash="success|Payee successfully deleted";
			}
			catch (Exception e) {
				flash="failure|Failed to delete payee";
			}
		}
		else {
			flash = "failure|Failed to delete payee - authorisation failure";		
		}
		
		return new RedirectView(String.format("%s/payee/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}	
}