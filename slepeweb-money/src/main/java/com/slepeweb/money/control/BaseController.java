package com.slepeweb.money.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.CategoryInput;
import com.slepeweb.money.bean.MultiCategoryCounter;
import com.slepeweb.money.bean.MultiSplitCounter;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SplitInput;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.Transaction;
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

	protected static final String FORM_MODE_ATTR = "_formMode";
	protected static final String JSON_ATTR = "_json";
	protected static final String SAVED_SEARCH_ATTR = "_ss";
	
	protected static final String CREATE_MODE = "create";
	protected static final String UPDATE_MODE = "update";
	protected static final String EXECUTE_MODE = "execute";
	protected static final String ADHOC_MODE = "adhoc";

	private static Logger LOG = Logger.getLogger(BaseController.class);
	
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
	
	protected List<SplitTransaction> readSplitsInput(HttpServletRequest req, long amountPlusOrMinus) {
		int index = 1;
		SplitTransaction st;
		Category c;
		String major, minor;
		List<SplitTransaction> splits = new ArrayList<SplitTransaction>();
		int numSplits = Integer.valueOf(req.getParameter("numsplits"));
		
		do {
			major = req.getParameter("major_" + index);
			if (StringUtils.isNotBlank(major)) {			
				minor = req.getParameter("minor_" + index);
				c = this.categoryService.get(major, minor);
				
				st = new SplitTransaction().
					setCategory(c).
					setMemo(req.getParameter("memo_" + index)).
					setAmount(Util.parsePounds(req.getParameter("amount_" + index)) * amountPlusOrMinus);
				
				// The transactionId for each ScheduledSplit will be assigned within TransactionService.save(t).
				// Only attempt to save split IFF category is specified
				splits.add(st);
			}
			
			index++;
		}
		while (index <= numSplits);
		
		return splits;
	}
	
	protected void populateTransAndSchedForm(ModelMap model, Transaction t, List<Account> allAccounts, String mode) {
		List<String> allMajors = this.categoryService.getAllMajorValues();

		model.addAttribute("_formMode", mode);
		model.addAttribute("_allAccounts", allAccounts);
		model.addAttribute("_allPayees", this.payeeService.getAll());
		model.addAttribute("_allMajorCategories", allMajors);
		
		if (t.getCategory() != null) {
			model.addAttribute("_allMinorCategories", this.categoryService.getAllMinorValues(t.getCategory().getMajor()));
		}
		
		List<SplitInput> arr = new ArrayList<SplitInput>();
		SplitInput split;
		int numVisible = t.getSplits().size();
		int numBlanks = 6;
		int count = 0;
		
		for (SplitTransaction st : t.getSplits()) {
			split = new SplitInput(st);
			split.
				setAllMajors(allMajors).
				setAllMinors(this.categoryService.getAllMinorValues(st.getCategory().getMajor())).
				setVisible(true);
			
			count++;
			if (count == numVisible) {
				split.setLastVisible(true);
			}
			
			arr.add(split);
		}
		
		Category noCategory = this.categoryService.getNoCategory();
		List<String> noMinors = new ArrayList<String>();
		
		for (int i = 0; i < numBlanks; i++) {
			split = new SplitInput();
			split.assimilate(noCategory);
			split.
				setAllMajors(allMajors).
				setAllMinors(noMinors).
				setVisible(false);
			
			arr.add(split);
		}
				
		model.addAttribute("_allSplits", arr);
	}
	
	@ModelAttribute(value="_ctxPath")
	protected String getWebContextPath(HttpServletRequest req) {
		return req.getContextPath();
	}
	
}
