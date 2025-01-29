package com.slepeweb.money.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.User;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.SavedSearchService;
import com.slepeweb.money.service.ScheduledTransactionService;
import com.slepeweb.money.service.SolrService4Money;
import com.slepeweb.money.service.TransactionService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BaseController {

	//private static Logger LOG = Logger.getLogger(BaseController.class);
	
	@Autowired protected PayeeService payeeService;
	@Autowired protected AccountService accountService;
	@Autowired protected CategoryService categoryService;
	@Autowired protected TransactionService transactionService;
	@Autowired protected ScheduledTransactionService scheduledTransactionService;
	@Autowired protected SolrService4Money solrService;
	@Autowired protected SavedSearchService savedSearchService;
	
	protected User getUser(HttpServletRequest req) {
		return (User) req.getSession().getAttribute(User.USER_ATTR);
	}
	
	protected List<Payee> getAllPayees() {
		List<Payee> payees = this.payeeService.getAll();
		if (payees.size() > 0 && StringUtils.isBlank(payees.get(0).getName())) {
			payees.remove(0);
		}
		return payees;
	}

	protected List<SavedSearch> filterSavedSearches(String type) {
		List<SavedSearch> searches = new ArrayList<SavedSearch>();
		
		for (SavedSearch ss : this.savedSearchService.getAll()) {
			if (ss.getType().equals(type)) {			
				searches.add(ss);
			}
		}
		
		return searches;
	}
	
	protected String storeSavedSearch(SavedSearch ss) {
		String flash;
		try {
			SavedSearch saved = this.savedSearchService.save(ss);
			ss.setId(saved.getId());
			flash = "success|Search successfully saved";
		}
		catch (Exception e) {
			flash = "failure|Failed to save search";
		}
		
		return flash;
	}
	
	
	@ModelAttribute(value="_ctxPath")
	protected String getWebContextPath(HttpServletRequest req) {
		return req.getContextPath();
	}
	
}
