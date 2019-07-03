package com.slepeweb.money.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.CategoryInput;
import com.slepeweb.money.bean.MultiCategoryCounter;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.SavedSearchService;
import com.slepeweb.money.service.ScheduledTransactionService;
import com.slepeweb.money.service.SolrService;
import com.slepeweb.money.service.TransactionService;

@Controller
public class BaseController {
	private static Logger LOG = Logger.getLogger(BaseController.class);
	public static final String USER = "_user";
	
	@Autowired protected PayeeService payeeService;
	@Autowired protected AccountService accountService;
	@Autowired protected CategoryService categoryService;
	@Autowired protected TransactionService transactionService;
	@Autowired protected ScheduledTransactionService scheduledTransactionService;
	@Autowired protected SolrService solrService;
	@Autowired protected SavedSearchService savedSearchService;
	
	protected List<Payee> getAllPayees() {
		List<Payee> payees = this.payeeService.getAll();
		if (payees.size() > 0 && StringUtils.isBlank(payees.get(0).getName())) {
			payees.remove(0);
		}
		return payees;
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
		
		return list;
	}
	
	// How many categories are in this group?
	protected int getNumCategoriesForGroup(List<MultiCategoryCounter> counters, int groupId) {
		for (MultiCategoryCounter c : counters) {
			if (c.getGroupId() == groupId) {
				return c.getCategoryCount();
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
		return hasAuthority(u, "MONEY_USER");
	}
	
	@ModelAttribute(value="_isAdmin")
	protected boolean isAdmin(@AuthenticationPrincipal User u) {
		return hasAuthority(u, "MONEY_ADMIN");
	}
	
	private boolean hasAuthority(User u, String name) {
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
