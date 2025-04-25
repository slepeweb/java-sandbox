package com.slepeweb.money.control;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.ReconciliationData;
import com.slepeweb.money.bean.RestResponse;
import com.slepeweb.money.bean.Transaction;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/transaction/reconcile")
public class ReconciliationController extends BaseController {
	
	public static final String RECONCILE_LIST = "reconcileList";
	public static final String PAUSED_RECONCILIATIONS = "pausedReconciliations";

	private static Logger LOG = Logger.getLogger(ReconciliationController.class);
	
	@RequestMapping(value="/form/{accountId}", method=RequestMethod.GET)	
	public String form(@PathVariable long accountId, HttpServletRequest req, ModelMap model) {
		
		// Has this account been part-reconciled in this http session?
		ReconciliationData d = (ReconciliationData) req.getSession().getAttribute(PAUSED_RECONCILIATIONS);
		
		// If session holds data fro a different account, remove that session data
		if (d != null && d.getAccountId() != accountId) {
			req.getSession().removeAttribute(PAUSED_RECONCILIATIONS);
			d = null;
		}
		
		Account a = this.accountService.get(accountId);
		List<Transaction> transactions = this.transactionService.getUnreconciled(accountId);
		
		if (d != null && d.getAccountId() == accountId) {
			for (Transaction t : transactions) {
				t.setProvisionallyReconciled(d.getTransactions().contains(t.getId()));
			}
			
			model.addAttribute(PAUSED_RECONCILIATIONS, d);
		}
		
		model.addAttribute("_tl", transactions);
		model.addAttribute("_account", a);
		
		return RECONCILE_LIST;
	}
	
	@RequestMapping(value="/pause/{accountId}", method=RequestMethod.POST)	
	public RedirectView pause(@PathVariable long accountId, HttpServletRequest req, ModelMap model) {
		
		/* 
		 * This method receives data from the same form as the reconcileSubmit method, and so the
		 * request parameters are the same, but interpreted differently. Specifically, the 'reconciledAmount'
		 * parameter in this context (ie pausing reconciliation) represents the target balance.
		 */
		Long targetBalance = Long.parseLong(req.getParameter("reconciledAmount"));
		ReconciliationData d = new ReconciliationData().setTarget(targetBalance).setAccountId(accountId);
		
		for (String idStr : req.getParameter("transactionIds").split(",")) {
			d.getTransactions().add(Long.parseLong(idStr));
		}
		
		req.getSession().setAttribute(PAUSED_RECONCILIATIONS, d);
		
		String flash = Util.encodeUrl("success|Reconciliation paused");
		return new RedirectView(String.format("%s/transaction/list/%d?flash=%s", 
				req.getContextPath(), accountId, flash));
	}
	
	@RequestMapping(value="/clear", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse clear(HttpServletRequest req, ModelMap model) {
		
		req.getSession().removeAttribute(PAUSED_RECONCILIATIONS);
		return new RestResponse().addMessage("Session data cleared");
	}
	
	@RequestMapping(value="/submit/{accountId}", method=RequestMethod.POST)	
	public RedirectView submit(@PathVariable long accountId, HttpServletRequest req, ModelMap model) {
		
		Account a = this.accountService.get(accountId);
		a.setReconciled(Long.parseLong(req.getParameter("reconciledAmount")));
		this.accountService.updateReconciled(a);
		
		Transaction t;
		String[] ids = req.getParameter("transactionIds").split(",");
		for (String idStr : ids) {
			t = this.transactionService.get(Long.parseLong(idStr));
			t.setReconciled(true);
			this.transactionService.updateReconciled(t.getId());
		}
		
		// Clear any session data
		req.getSession().removeAttribute(PAUSED_RECONCILIATIONS);
		
		String msg = String.format("%d transactions reconciled", ids.length);
		LOG.info(msg);
		
		String flash = Util.encodeUrl(String.format("success|%s", msg));
		return new RedirectView(String.format("%s/transaction/list/%d?flash=%s", 
				req.getContextPath(), a.getId(), flash));
	}
}