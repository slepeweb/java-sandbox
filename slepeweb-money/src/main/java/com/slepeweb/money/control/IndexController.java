package com.slepeweb.money.control;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.solr.SolrParams;

@Controller
public class IndexController extends BaseController {
	
	//private static Logger LOG = Logger.getLogger(IndexController.class);
	
	@RequestMapping(value="/index/by/dates", method=RequestMethod.GET)
	public String indexByDates(ModelMap model) {
 
		return "indexForm"; 
	}
		
	@RequestMapping(value="/index/by/dates", method=RequestMethod.POST)	
	public RedirectView indexByDatesAction(HttpServletRequest req) { 
		String startStr = req.getParameter("from");
		String endStr = req.getParameter("to");
		Calendar mark = Util.today();
		mark.add(Calendar.DATE, 1);
		Util.zeroTimeOfDay(mark);
		
		Date start = StringUtils.isBlank(startStr) ? 
				new Date(0L) :
					Util.parseSolrDate(startStr + SolrParams.START_OF_DAY);
			
		Date end = StringUtils.isBlank(endStr) ? 
				mark.getTime() :
					Util.parseSolrDate(endStr + SolrParams.END_OF_DAY);
		
		
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
	public RedirectView indexByAccount(@PathVariable long accountId, HttpServletRequest req) { 
		Account a = this.accountService.get(accountId);
		this.solrService.removeTransactionsByAccount(a.getName());
		this.solrService.save(this.transactionService.getTransactionsForAccount(accountId));
		return new RedirectView(String.format("%s/search/?flash=%s", 
				req.getContextPath(), Util.encodeUrl("success|Indexing complete")));
	}
	
}