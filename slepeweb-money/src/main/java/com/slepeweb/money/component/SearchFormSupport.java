package com.slepeweb.money.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.common.util.JsonUtil;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Category_;
import com.slepeweb.money.bean.Category_Group;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Payee;
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
	
	public static final String CREATE_MODE = "add";
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
		
		Category_Group cg;
		
		if (params.getCategoryGroup() == null) {
			cg = this.formSupport.populateCategory_Group(null, SearchCategory.class);	
			params.setCategoryGroup(cg);
		}
		else {
			cg = params.getCategoryGroup();
			cg.setAllCategoriesVisible();
			this.formSupport.addEmptyCategoryIf(cg);
		}
		
		cg.setVisible(true);

		model.addAttribute(SAVED_SEARCH_ATTR, ss);		
		model.addAttribute(PARAMS_ATTR, params);		
		model.addAttribute(ALL_ACCOUNTS_ATTR, this.accountService.getAll(true));
		model.addAttribute(ALL_PAYEES_ATTR, this.payeeService.getAll());
		model.addAttribute(JSON_ATTR, Util.encodeUrl(JsonUtil.toJson(params)));		
		model.addAttribute(FORM_MODE_ATTR, formMode);
		model.addAttribute(CategoryController.ALL_MAJOR_CATEGORIES_ATTR, this.categoryService.getAllMajorValues());
	}
	
	
	public SavedSearchSupport processFormSubmission(HttpServletRequest req, SavedSearch ss) {
		
		// Read the search parameters from the submitted form
		SolrParams params = readSearchCriteria(req);
		
		if (! params.isTransfer()) {
			// Create a new Category_Group using the submitted form data, and add it to the set IFF populated
			params.setCategoryGroup(this.formSupport.readCategoryInputs(req));			
		}
		
		// Update the SavedSearch object, which gets saved to the db
		ss.
			setName(req.getParameter("name")).
			setDescription(req.getParameter("description")).
			setJson(JsonUtil.toJson(params));
		
		// This support object simplifies matters ???
		SavedSearchSupport sss = new SavedSearchSupport().
				setRequest(req).
				setSavedSearch(ss).
				setSolrParams(params).
				setMode(req.getParameter("formMode"));
		
		if (sss.getMode().equals(ADHOC_MODE)) {			
			sss.setAdhoc(true);
		}
		else {
			sss.setSave(isOption("save", req) || isOption("save-execute", req));
			sss.setExecute(isOption("execute", req) || isOption("save-execute", req));
		}
		
		return sss;
	}
	
	public SolrParams readSearchCriteria(HttpServletRequest req) {
		
		// 'payee' takes precedence over 'transferAccount'
		String payeeName = req.getParameter("payee");
		String transferAccount = req.getParameter("transferAccount");
		
		if (StringUtils.isNotBlank(payeeName)) {
			transferAccount = null;
		}

		SolrParams p = new SolrParams(new SolrConfig());
		p.setAccountId(req.getParameter("account"));
		p.setPayeeName(payeeName);
		p.setTransferAccountId(transferAccount);
		p.setTransferDirection(req.getParameter("transferDirection"));
		p.setMemo(req.getParameter("memo"));
		p.setPeriodValue(req.getParameter("periodvalue"));
		p.setPeriodUnits(req.getParameter("periodunits"));
		p.setFrom(req.getParameter("from"));
		p.setTo(req.getParameter("to"));
		p.setDebit(req.getParameter("debitorcredit"));
		p.setFromAmount(req.getParameter("from-amount"));
		p.setToAmount(req.getParameter("to-amount"));
		p.setPageSize(req.getParameter("pageSize"));
		p.setPageNum(1);
				
		payeeName2Id(p);
		return p;
	}	

	public long executeSearch(SolrParams params, ModelMap model) {
		SolrResponse<FlatTransaction> resp = this.solrService4Money.query(params);
		model.addAttribute(SearchFormSupport.SEARCH_RESPONSE_ATTR, resp);		
		long credit = 0;
		for (FlatTransaction ft : resp.getResults()) {
			credit += ft.getAmount();
		}
		
		model.addAttribute("_totalCredit", credit);
		return credit;
	}
	
	public RedirectView redirect2Execute(SavedSearchSupport supp) {
		return redirect(
				String.format(
						"/%s/get/%d?flash=%s", 
						"search",
						supp.getSavedSearch().getId(),
						Util.encodeUrl(supp.getFlash())));
	}
	
	public RedirectView redirect2Adhoc(SavedSearchSupport supp) {
		return redirect(
				String.format(
						"/%s/get/adhoc?flash=%s", 
						"search",
						Util.encodeUrl(supp.getFlash())));
	}
	
	public RedirectView redirect2List(SavedSearchSupport supp) {
		return redirect(
				String.format("/%s/list?flash=%s", 
						"search",
						Util.encodeUrl(supp.getFlash())));
	}
	
	private RedirectView redirect(String url) {
		return new RedirectView(url, true, true, false);
	}
	
	public boolean isOption(String option, HttpServletRequest req) {
		String p = req.getParameter("submit-option");
		return p != null && StringUtils.containsIgnoreCase(p, option);
	}
	
	
	
	public boolean convertId2Name(SolrParams params) {
		boolean missingEntity = false;
		
		if (params.getPayeeId() != null && params.getPayeeId().longValue() > 0) {
			Payee p = this.payeeService.get(params.getPayeeId());
			missingEntity = missingEntity || p == null;
			params.setPayeeName(p != null ? p.getName() : "");
		}
		
		Category c;
		Category_Group group = params.getCategoryGroup();
		
		if (group == null) {
			return missingEntity;
		}
		
		for (Category_ c_ : group.getCategories()) {
			c = this.categoryService.get(c_.getId());
			
			if (c == null) {
				// This is happening while the json properties are being updated
				missingEntity = true;
				continue;
			}
			
			c_.setMajor(c.getMajor());
			c_.setMinor(c.getMinor());
			group.addOptions(c.getMajor(), this.categoryService.getAllMinorValues(c.getMajor()));
		}
		
		return missingEntity;
	}
	
	public void payeeName2Id(SolrParams params) {
		if (StringUtils.isNotBlank(params.getPayeeName())) {
			Payee p = this.payeeService.get(params.getPayeeName());
			if (p != null) {
				params.setPayeeId(p.getId());
			}
		}
	}
}
