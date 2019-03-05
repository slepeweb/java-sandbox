package com.slepeweb.money.control;

import java.util.Date;
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
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.SolrService;
import com.slepeweb.money.service.TransactionService;

@Controller
public class SearchController extends BaseController {
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private TransactionService transactionService;
	@Autowired private SolrService solrService;
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String form(ModelMap model) {
		
		model.addAttribute("_allAccounts", this.accountService.getAll(true));
		
		List<Payee> payees = this.payeeService.getAll();
		if (payees.size() > 0 && StringUtils.isBlank(payees.get(0).getName())) {
			payees.remove(0);
		}
		model.addAttribute("_allPayees", payees);
		
		List<String> categories = this.categoryService.getAllMajorValues();
		if (categories.size() > 0 && StringUtils.isBlank(categories.get(0))) {
			categories.remove(0);
		}
		model.addAttribute("_allMajorCategories", categories);
		
		return "advancedSearch";
	}
	
	@RequestMapping(value="/search", method=RequestMethod.POST)
	public String resultsDefault(HttpServletRequest req, ModelMap model) {
		return results(1, req, model);
	}
	
	@RequestMapping(value="/search/{page}", method=RequestMethod.GET)
	public String results(@PathVariable int page, HttpServletRequest req, ModelMap model) {
		
		// Payee may be specified by either name or id, but not both!
		SolrParams params = 
			new SolrParams(new SolrConfig()).
			setAccountId(req.getParameter("accountId")).
			setPayeeId(req.getParameter("payeeId")).
			setPayeeName(req.getParameter("payee")).
			setMajorCategory(req.getParameter("category")).
			setMemo(req.getParameter("memo")).
			setFrom(req.getParameter("from")).
			setTo(req.getParameter("to")).
			setPageNum(page);
				
		model.addAttribute("_response", this.solrService.query(params));				
		form(model);
		return "advancedSearch";
	}	
	
	@RequestMapping(value="/index/by/dates", method=RequestMethod.GET)
	public String indexByDates(ModelMap model) {
 
		return "indexForm"; 
	}
		
	@RequestMapping(value="/index/by/dates", method=RequestMethod.POST)	
	public RedirectView indexByDatesAction(HttpServletRequest req) { 
		String startStr = req.getParameter("from");
		String endStr = req.getParameter("to");
		
		if (StringUtils.isBlank(startStr) || StringUtils.isBlank(endStr)) {
			return new RedirectView(String.format("%s/search/?flash=%s", 
					req.getContextPath(), Util.encodeUrl("failure|Indexing not possible - Incomplete dates")));	
		}
		
		Date start = Util.parseSolrDate(startStr + SolrParams.START_OF_DAY);
		Date end = Util.parseSolrDate(endStr + SolrParams.END_OF_DAY);
		this.solrService.removeTransactionsByDate(start, end);
		boolean ok = this.solrService.save(this.transactionService.getTransactionsByDate(start, end));
		
		return new RedirectView(String.format("%s/search/?flash=%s", 
				req.getContextPath(), Util.encodeUrl(
						ok ? "success|Indexing complete" : "failure|Problem indexing by dates")));
	}
	
	@RequestMapping(value="/index/all")	
	public RedirectView indexEverything(HttpServletRequest req) { 
		this.solrService.removeAllTransactions();
		this.solrService.save(this.transactionService.getAll());
		return new RedirectView(String.format("%s/search/?flash=%s", 
				req.getContextPath(), Util.encodeUrl("success|Indexing complete")));
	}
	
	@RequestMapping(value="/index/by/account/{accountId}")	
	public RedirectView index(@PathVariable long accountId, HttpServletRequest req) { 
		Account a = this.accountService.get(accountId);
		this.solrService.removeTransactionsByAccount(a.getName());
		this.solrService.save(this.transactionService.getTransactionsForAccount(accountId));
		return new RedirectView(String.format("%s/search/?flash=%s", 
				req.getContextPath(), Util.encodeUrl("success|Indexing complete")));
	}
	
}