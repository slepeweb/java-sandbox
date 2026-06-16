package com.slepeweb.money.control;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.History;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.User;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.ChartService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.SavedSearchService;
import com.slepeweb.money.service.ScheduledTransactionService;
import com.slepeweb.money.service.SolrService4Money;
import com.slepeweb.money.service.TransactionService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BaseController {

	private static Logger LOG = Logger.getLogger(BaseController.class);
	protected static final String HISTORY_ATTR = "history";
	protected static final String ALL_MAJORS = "_allMajorCategories";
	
	@Autowired protected PayeeService payeeService;
	@Autowired protected AccountService accountService;
	@Autowired protected CategoryService categoryService;
	@Autowired protected TransactionService transactionService;
	@Autowired protected ScheduledTransactionService scheduledTransactionService;
	@Autowired protected SolrService4Money solrService;
	@Autowired protected SavedSearchService savedSearchService;
	@Autowired protected ChartService chartService;
	
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
	
	protected History getHistory(HttpServletRequest req) {
		History h = (History) req.getSession().getAttribute(HISTORY_ATTR);
		if (h == null) {
			h = new History();
			req.getSession().setAttribute(HISTORY_ATTR, h);
		}
		return h;
	}
	
	public Payee getPayeeElseNew(String name) {
		Payee p = this.payeeService.get(name);
		
		if (p != null) {
			return p;
		}
		
		try {
			p = this.payeeService.save(new Payee().setName(name));
			LOG.info(String.format("Created new payee: %s", name));
			return p;
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to save new payee %s", name));
		}
		
		return null;
	}
	
	public Category getCategoryElseNew(String major, String minor) {
		Category c = this.categoryService.get(major, minor);
		
		if (c != null) {
			return c;
		}
		
		try {
			c = this.categoryService.save(new Category(major, minor));
			LOG.info(String.format("Created new category: %s", c));
			return c;
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to save new category %s/%s", major, minor));
		}
		
		return null;
	}
	

	
	@ModelAttribute(value="_ctxPath")
	protected String getWebContextPath(HttpServletRequest req) {
		return req.getContextPath();
	}
	
}
