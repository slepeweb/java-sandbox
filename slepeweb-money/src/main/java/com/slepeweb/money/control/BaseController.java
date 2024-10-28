package com.slepeweb.money.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.CategoryInput;
import com.slepeweb.money.bean.MultiCategoryCounter;
import com.slepeweb.money.bean.MultiSplitCounter;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SplitInput;
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

	protected static final String FORM_MODE_ATTR = "_formMode";
	protected static final String JSON_ATTR = "_json";
	protected static final String SAVED_SEARCH_ATTR = "_ss";
	
	protected static final String CREATE_MODE = "create";
	protected static final String UPDATE_MODE = "update";
	protected static final String EXECUTE_MODE = "execute";
	protected static final String ADHOC_MODE = "adhoc";

	private static Logger LOG = Logger.getLogger(BaseController.class);
	public static final String USER = "_user";
	public static final String ADMIN_ROLE = "MONEY_ADMIN";
	public static final String USER_ROLE = "MONEY_USER";
	
	@Autowired protected PayeeService payeeService;
	@Autowired protected AccountService accountService;
	@Autowired protected CategoryService categoryService;
	@Autowired protected TransactionService transactionService;
	@Autowired protected ScheduledTransactionService scheduledTransactionService;
	@Autowired protected SolrService4Money solrService;
	@Autowired protected SavedSearchService savedSearchService;
	
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
	
	/*
	 * This method allows us to de-serialize a json string into a list of objects. This is a neater way
	 * than returning a convenience object with a single property that is the list we are after.
	 * 
	 * (I don't know how this works, but it does!)
	 */
	protected static <T> T fromJson(final TypeReference<T> type, final String jsonPacket) {

		T data = null;
		try {
			data = new ObjectMapper().readValue(jsonPacket, type);
		} catch (Exception e) {
			// Handle the problem
		}
		return data;
	}
	
	protected static String toJson(Object o) {

		String s = null;
		try {
			s = new ObjectMapper().writeValueAsString(o);
		} catch (Exception e) {
			LOG.error("Jackson marshalling error", e);
		}
		return s;
	}
	
	protected List<CategoryInput> readMultiCategoryInput(HttpServletRequest req, int groupId, int numCategories) {
		int m = 0;	
		String major, minor;
		boolean excluded;
		CategoryInput cat;
		List<CategoryInput>	list = new ArrayList<CategoryInput>();
		
		if (numCategories > 0) {
			for (int i = 1; i < 10; i++) {
				major = req.getParameter(String.format("major-%d-%d", groupId, i));
				minor = req.getParameter(String.format("minor-%d-%d", groupId, i));
				excluded = Util.isPositive(req.getParameter(String.format("logic-%d-%d", groupId, i)));
				
				if (StringUtils.isNotBlank(major)) {					
					cat = new CategoryInput().
						setMajor(major).
						setMinor(minor).
						setExclude(excluded).
						setOptions(this.categoryService.getAllMinorValues(major));
					
					list.add(cat);
					
					if (++m >= numCategories) {
						break;
					}
				}
			}
		}
		
		return list;
	}
	
	protected List<SplitInput> readMultiSplitInput(HttpServletRequest req, MultiSplitCounter counters) {
		int count = 0;	
		String major, minor;
		SplitInput cat;
		List<SplitInput> list = new ArrayList<SplitInput>();
		
		if (counters.getLastSplitId() > 0) {
			for (int i = 1; i < 10; i++) {
				major = req.getParameter(String.format("major-%d", i));
				minor = req.getParameter(String.format("minor-%d", i));
				
				if (StringUtils.isNotBlank(major)) {					
					cat = new SplitInput();
					cat.
						setMajor(major).
						setMinor(minor).
						setOptions(this.categoryService.getAllMinorValues(major));
					
					cat.
						setMemo(req.getParameter(String.format("memo-%d", i))).
						setAmount(req.getParameter(String.format("amount-%d", i)));
					
					list.add(cat);
					count++;
					
					if (i >= counters.getLastSplitId()) {
						break;
					}
				}
			}
		}
		
		if (count != counters.getSplitCount()) {
			LOG.warn(String.format("Split counters mis-match: Expected %d splits, but identified %d", 
					counters.getSplitCount(), count));
		}
		
		return list;
	}
	
	// How many categories are in this group?
	protected int getNumCategoriesForGroup(List<MultiCategoryCounter> counters, int groupId) {
		if (counters != null) {
			for (MultiCategoryCounter c : counters) {
				if (c.getGroupId() == groupId) {
					return c.getCategoryCount();
				}
			}
		}
		
		return 0;
	}
	
	@ModelAttribute(value=USER)
	protected User getUser(@AuthenticationPrincipal User u) {
		LOG.trace(String.format("Model attribute (_user): [%s]", u));
		return u;
	}
	
	
	@ModelAttribute(value="_isUser")
	protected boolean isUser(@AuthenticationPrincipal User u) {
		return hasAuthority(u, USER_ROLE);
	}
	
	@ModelAttribute(value="_isAdmin")
	protected boolean isAdmin(@AuthenticationPrincipal User u) {
		return hasAuthority(u, ADMIN_ROLE);
	}
	
	protected boolean hasAuthority(User u, String name) {
		if (u != null) {
			for (GrantedAuthority auth : u.getAuthorities()) {
				if (auth.getAuthority().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@ModelAttribute(value="_ctxPath")
	protected String getWebContextPath(HttpServletRequest req) {
		return req.getContextPath();
	}
	
}
