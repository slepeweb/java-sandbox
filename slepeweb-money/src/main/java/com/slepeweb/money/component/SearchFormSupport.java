package com.slepeweb.money.component;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Category_Group;
import com.slepeweb.money.bean.Category_GroupSet;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SearchCategory;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.control.CategoryController;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class SearchFormSupport {
	
	public static Logger LOG = Logger.getLogger(SearchFormSupport.class);
	public static final String ALL_ACCOUNTS_ATTR = "_allAccounts";
	public static final String ALL_PAYEES_ATTR = "_allPayees";
	public static final String CATEGORY_GROUP_ATTR = "_categoryGroup";
	public static final String PARAMS_ATTR = "_params";
	public static final String SEARCH_LIST_ATTR = "_searches";
	public static final String SEARCH_RESPONSE_ATTR = "_response";
	
	public static final String ADVANCED_TYPE = "advanced";
	
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

	public void populateForm(
			SavedSearch ss, SolrParams params, String formMode, ModelMap model) {
		
		List<String> allMajors = this.categoryService.getAllMajorValues();
		Category_GroupSet cgs = new Category_GroupSet("Search", Category_GroupSet.SEARCH_CTX, allMajors);
		Category_Group cg;
		
		if (params.getCategoryGroup() == null) {
			cg = this.formSupport.populateCategory_Group(1, "Categories", null, SearchCategory.class, cgs);			
			params.setCategoryGroup(cg);
		}
		else {
			cg = params.getCategoryGroup();
			cg.setAllCategoriesVisible();
			this.formSupport.addEmptyCategories(cg);
		}
		
		cg.setVisible(true);
		cgs.getGroups().add(cg);

		model.addAttribute(SAVED_SEARCH_ATTR, ss);		
		model.addAttribute(PARAMS_ATTR, params);		
		model.addAttribute(CATEGORY_GROUP_ATTR, cgs);				
		model.addAttribute(ALL_ACCOUNTS_ATTR, this.accountService.getAll(true));
		model.addAttribute(ALL_PAYEES_ATTR, this.payeeService.getAll());
		model.addAttribute(JSON_ATTR, Util.encodeUrl(toJson(params)));		
		model.addAttribute(FORM_MODE_ATTR, formMode);
		model.addAttribute(CategoryController.ALL_MAJOR_CATEGORIES_ATTR, allMajors);
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
