package com.slepeweb.money.component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Category_Group;
import com.slepeweb.money.bean.Category_GroupSet;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SavedSearchSupport;
import com.slepeweb.money.bean.SearchCategory;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;
import com.slepeweb.money.control.CategoryController;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.SolrService4Money;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class SearchFormSupport {
	
	public static Logger LOG = Logger.getLogger(SearchFormSupport.class);
	
	public static final String CREATE_MODE = "create";
	public static final String UPDATE_MODE = "update";
	public static final String EXECUTE_MODE = "execute";
	public static final String ADHOC_MODE = "adhoc";

	public static final String SEARCH_CTX = "search";
	public static final String CHART_CTX = "chart";

	public static final String ALL_ACCOUNTS_ATTR = "_allAccounts";
	public static final String ALL_PAYEES_ATTR = "_allPayees";
	public static final String CATEGORY_GROUP_ATTR = "_categoryGroup";
	public static final String PARAMS_ATTR = "_params";
	public static final String SEARCH_LIST_ATTR = "_searches";
	public static final String SEARCH_RESPONSE_ATTR = "_response";
	
	public static final String FORM_VIEW = "searchForm";
	public static final String LIST_VIEW = "searchList";
	public static final String RESULTS_VIEW = "searchResults";
	
	public static final String FORM_MODE_ATTR = "_formMode";
	public static final String JSON_ATTR = "_json";
	public static final String SAVED_SEARCH_ATTR = "_ss";

	@Autowired private FormSupport formSupport;
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private SolrService4Money solrService4Money;

	public void populateForm(
			SavedSearch ss, SolrParams params, String formMode, ModelMap model) {
		
		List<String> allMajors = this.categoryService.getAllMajorValues();
		Category_GroupSet cgs = new Category_GroupSet("Search", SearchFormSupport.SEARCH_CTX, allMajors);
		Category_Group cg;
		
		if (params.getCategoryGroup() == null) {
			cg = this.formSupport.populateCategory_Group(1, "Categories", null, SearchCategory.class);	
			params.setCategoryGroup(cg);
		}
		else {
			cg = params.getCategoryGroup();
			cg.setAllCategoriesVisible();
			this.formSupport.addEmptyCategories(cg);
		}
		
		cg.setVisible(true);
		cgs.addGroup(cg);

		model.addAttribute(SAVED_SEARCH_ATTR, ss);		
		model.addAttribute(PARAMS_ATTR, params);		
		model.addAttribute(CATEGORY_GROUP_ATTR, cgs);				
		model.addAttribute(ALL_ACCOUNTS_ATTR, this.accountService.getAll(true));
		model.addAttribute(ALL_PAYEES_ATTR, this.payeeService.getAll());
		model.addAttribute(JSON_ATTR, Util.encodeUrl(toJson(params)));		
		model.addAttribute(FORM_MODE_ATTR, formMode);
		model.addAttribute(CategoryController.ALL_MAJOR_CATEGORIES_ATTR, allMajors);
	}
	
	
	public SavedSearchSupport processFormSubmission(HttpServletRequest req, SavedSearch ss) {
		List<String> allMajors = this.categoryService.getAllMajorValues();
		Category_GroupSet cgs = new Category_GroupSet("Splits", SEARCH_CTX, allMajors);
		
		// Create a new Category_Group using the submitted form data, and add it to the set IFF populated
		this.formSupport.readCategoryInputs(req, 1, cgs);
		
		// Read the remaining search parameters from the submitted form
		SolrParams params = readSearchCriteria(req);
		
		/*
		 *  Search functionality is based on a single group of categories.
		 *  Combine the the first Category_Group into the SolrParams. It 
		 *  is the SolrParams object that will get stored in the db as a json string.
		 */
		params.setCategoryGroup(cgs.getGroups().get(0));
			
		// Update the SavedSearch object, which gets saved to the db
		ss.
			setType(SEARCH_CTX).
			setName(req.getParameter("name")).
			setDescription(req.getParameter("description")).
			setJson(toJson(params)).
			setSaved(new Timestamp(new Date().getTime()));
		
		// This support object simplifies matters ???
		SavedSearchSupport sss = new SavedSearchSupport().
				setRequest(req).
				setSavedSearch(ss).
				setSolrParams(params).
				setMode(req.getParameter("formMode"));
		
		if (sss.getMode().equals(ADHOC_MODE) ||
				sss.getMode().equals(CREATE_MODE) && isOption("execute", req)) {
			
			sss.setAdhoc(true);
		}
		else {
			sss.
				setSave(isOption("save", req)).
				setExecute(isOption("execute", req));
		}
		
		return sss;
	}
	
	public SolrParams readSearchCriteria(HttpServletRequest req) {
		// Payee may be specified by either name or id, but not both!
		return 
			new SolrParams(new SolrConfig()).
			setAccountId(req.getParameter("account")).
			setPayeeId(req.getParameter("payeeId")).
			setPayeeName(req.getParameter("payee")).
			setMemo(req.getParameter("memo")).
			setFrom(req.getParameter("from")).
			setTo(req.getParameter("to")).
			setDebit(req.getParameter("debitorcredit")).
			setFromAmount(req.getParameter("from-amount")).
			setToAmount(req.getParameter("to-amount")).
			setPageSize(req.getParameter("pageSize")).
			setPageNum(1);
	}	

	public void executeSearch(SolrParams params, ModelMap model) {
		SolrResponse<FlatTransaction> resp = this.solrService4Money.query(params);
		model.addAttribute(SearchFormSupport.SEARCH_RESPONSE_ATTR, resp);		
		long credit = 0;
		for (FlatTransaction ft : resp.getResults()) {
			credit += ft.getAmount();
		}
		
		model.addAttribute("_totalCredit", credit);		
	}
	
	public RedirectView redirect2Execute(SavedSearchSupport supp) {
		return redirect(
				String.format(
						"/%s/get/%d?flash=%s", 
						supp.getSavedSearch().getType(),
						supp.getSavedSearch().getId(),
						Util.encodeUrl(supp.getFlash())));
	}
	
	public RedirectView redirect2Adhoc(SavedSearchSupport supp) {
		return redirect(
				String.format(
						"/%s/get/adhoc?flash=%s", 
						supp.getSavedSearch().getType(),
						Util.encodeUrl(supp.getFlash())));
	}
	
	public RedirectView redirect2List(SavedSearchSupport supp) {
		return redirect(
				String.format("/%s/list?flash=%s", 
						supp.getSavedSearch().getType(),
						Util.encodeUrl(supp.getFlash())));
	}
	
	private RedirectView redirect(String url) {
		return new RedirectView(url, true, true, false);
	}
	
	public boolean isOption(String option, HttpServletRequest req) {
		String p = req.getParameter("submit-option");
		return p != null && StringUtils.containsIgnoreCase(p, option);
	}
	

	/*
	 * This method allows us to de-serialize a json string into a list of objects. This is a neater way
	 * than returning a convenience object with a single property that is the list we are after.
	 * 
	 * (I don't know how this works, but it does!)
	 */
	public <T> T fromJson(final TypeReference<T> type, final String jsonPacket) {

		T data = null;
		try {
			data = new ObjectMapper().readValue(jsonPacket, type);
		} catch (Exception e) {
			LOG.error("json de-serialisation error: ", e);
		}
		return data;
	}
	
	public String toJson(Object o) {

		String s = null;
		try {
			s = new ObjectMapper().writeValueAsString(o);
		} catch (Exception e) {
			LOG.error("Jackson marshalling error", e);
		}
		return s;
	}
}
