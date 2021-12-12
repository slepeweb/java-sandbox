package com.slepeweb.money.control;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.CategoryGroup;
import com.slepeweb.money.bean.CategoryInput;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.MultiCategoryCounter;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SavedSearchSupport;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;

@Controller
@RequestMapping(value="/search")
public class SearchController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(SearchController.class);
	
	private static final String ALL_ACCOUNTS_ATTR = "_allAccounts";
	private static final String ALL_PAYEES_ATTR = "_allPayees";
	private static final String CATEGORY_GROUP_ATTR = "_categoryGroup";
	private static final String PARAMS_ATTR = "_params";
	private static final String SEARCH_LIST_ATTR = "_searches";
	private static final String SEARCH_RESPONSE_ATTR = "_response";
	
	private static final String ADVANCED_TYPE = "advanced";
	
	private static final String FORM_VIEW = "searchForm";
	private static final String LIST_VIEW = "searchList";
	private static final String RESULTS_VIEW = "searchResults";
	
	private static final int ADHOC_ID = -1;
	
	/*
	 * 'Charts' and 'searches' use solr to query data, so the solr
	 * index needs to be up to date for results to be accurate.
	 */
	
	private SavedSearch setSavedSearch(SavedSearch ss, String name, String description, SolrParams params) {
		return ss.
			setType(ADVANCED_TYPE).
			setName(name).
			setDescription(description).
			setJson(toJson(params)).
			setSaved(new Timestamp(new Date().getTime()));
	}

	private CategoryGroup emptyCategoryGroup() {
		CategoryGroup grp = new CategoryGroup().setId(1).setLabel("unset");
		grp.getCategories().add(new CategoryInput().setId(1));
		return grp;
	}

	private void setCommonModelAttributes(
			SavedSearch ss, SolrParams params, String formMode, ModelMap model) {
		
		model.addAttribute(SAVED_SEARCH_ATTR, ss);		
		model.addAttribute(PARAMS_ATTR, params);		
		model.addAttribute(CATEGORY_GROUP_ATTR, getCategoryGroup(params.getCategories()));				
		model.addAttribute(ALL_ACCOUNTS_ATTR, this.accountService.getAll(true));
		model.addAttribute(ALL_PAYEES_ATTR, getAllPayees());
		model.addAttribute(JSON_ATTR, Util.encodeUrl(toJson(params)));		
		model.addAttribute(FORM_MODE_ATTR, formMode);
		model.addAttribute(CategoryController.ALL_MAJOR_CATEGORIES_ATTR, 
				this.categoryService.getAllMajorValues());
	}
	
	private void executeSearch(SolrParams params, ModelMap model) {
		SolrResponse<FlatTransaction> resp = this.solrService.query(params);
		model.addAttribute(SEARCH_RESPONSE_ATTR, resp);		
		long credit = 0;
		for (FlatTransaction ft : resp.getResults()) {
			credit += ft.getAmount();
		}
		
		model.addAttribute("_totalCredit", credit);		
	}
	
	private SolrParams getSearchCriteriaFromRequest(HttpServletRequest req) {
		// Payee may be specified by either name or id, but not both!
		return 
			new SolrParams(new SolrConfig()).
			setAccountId(req.getParameter("accountId")).
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
	
	private CategoryGroup getCategoryGroup(List<Category> categories) {
		CategoryGroup grp = new CategoryGroup().setId(1);
		CategoryInput ci;
		
		if (categories != null && categories.size() > 0) {
			for (Category c : categories) {
				ci = new CategoryInput().
						setMajor(c.getMajor()).
						setMinor(c.getMinor()).
						setOptions(this.categoryService.getAllMinorValues(c.getMajor()));
				ci.setExclude(c.isExclude());
				grp.getCategories().add(ci);
						
			}
			
			return grp;
		}
		
		return emptyCategoryGroup();
	}

	private static SolrParams toSolrParams(String jsonPacket) {
	
		try {
			return new ObjectMapper().readValue(jsonPacket, SolrParams.class);
		} catch (Exception e) {
			LOG.error("Jackson marshalling error", e);
		}
		return null;
	}

	private List<Category> getCategoriesFromRequest(HttpServletRequest req) {
		String countersJson = req.getParameter("counterStore");
		List<MultiCategoryCounter> counters = fromJson(new TypeReference<List<MultiCategoryCounter>>() {}, countersJson);
		int groupId = 1;
		List<CategoryInput> inputs = readMultiCategoryInput(req, groupId, getNumCategoriesForGroup(counters, groupId));
		CategoryGroup grp = new CategoryGroup().setId(1).setCategories(inputs);
		return grp.toCategoryList();
	}
	
	private SavedSearchSupport processFormSubmission(HttpServletRequest req, SavedSearch ss) {
		SolrParams params = getSearchCriteriaFromRequest(req);
		params.setCategories(getCategoriesFromRequest(req));
				
		ss = setSavedSearch(
				ss,
				req.getParameter("name"),
				req.getParameter("description"),
				params);
		
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
	
	private RedirectView redirect2Execute(SavedSearchSupport supp) {
		return redirect(
				String.format(
						"/search/get/%d?flash=%s", 
						supp.getSavedSearch().getId(),
						Util.encodeUrl(supp.getFlash())));
	}
	
	private RedirectView redirect2Adhoc(SavedSearchSupport supp) {
		return redirect(
				String.format(
						"/search/get/adhoc?flash=%s", 
						Util.encodeUrl(supp.getFlash())));
	}
	
	private RedirectView redirect2List(SavedSearchSupport supp) {
		return redirect(
				String.format("/search/list?flash=%s", 
						Util.encodeUrl(supp.getFlash())));
	}
	
	private RedirectView redirect(String url) {
		return new RedirectView(url, true, true, false);
	}
	
	private boolean isOption(String option, HttpServletRequest req) {
		String p = req.getParameter("submit-option");
		return p != null && StringUtils.containsIgnoreCase(p, option);
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String list(ModelMap model) {
		model.addAttribute(SEARCH_LIST_ATTR, filterSavedSearches(ADVANCED_TYPE));
		return LIST_VIEW;
	}

	// Empty search definition form, for adding a new search
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public String create(ModelMap model) {
		return emptyForm(CREATE_MODE, model);		
	}

	// Empty search definition form, for adding a new search
	@RequestMapping(value="/adhoc", method=RequestMethod.GET)
	public String adhoc(ModelMap model) {
		return emptyForm(ADHOC_MODE, model);		
	}
	
	private String emptyForm(String mode, ModelMap model) {
		setCommonModelAttributes(null, new SolrParams(new SolrConfig()), mode, model);		
		model.addAttribute(CATEGORY_GROUP_ATTR, emptyCategoryGroup());				
		return FORM_VIEW;
	}

	// Save a newly created search
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public RedirectView save(HttpServletRequest req, ModelMap model) {
		return save(req, new SavedSearch());
	}
	
	// Update an existing search on form submission
	@RequestMapping(value="/save/{id}", method=RequestMethod.POST)
	public RedirectView save(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		return save(req, this.savedSearchService.get(id));
	}

	private RedirectView save(HttpServletRequest req, SavedSearch ss) {
		SavedSearchSupport supp = processFormSubmission(req, ss);
		
		if (supp.isAdhoc()) {
			ss.setId(ADHOC_ID);
			storeSavedSearch(ss);
			return redirect2Adhoc(supp.setFlash(""));
		}
		else {
			if (supp.isSave()) {
				supp.setFlash(storeSavedSearch(ss));
			}
			else {
				supp.setFlash("success|Search NOT saved");
			}
			
			if (supp.isExecute()) {
				if (! supp.isSave()) {
					supp.setFlash("");
				}
				return redirect2Execute(supp);
			}
		}
		
		return redirect2List(supp);
	}
	
	// Form to edit an existing search
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		
		SavedSearch ss = this.savedSearchService.get(id);
		SolrParams params = toSolrParams(ss.getJson());
		
		model.addAttribute("_numDeletableTransactions", 0);
		setCommonModelAttributes(ss, params, UPDATE_MODE, model);	
		return FORM_VIEW;
	}
	
	// Delete an existing search
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public RedirectView delete(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		SavedSearch ss = this.savedSearchService.get(id);
		String flash;
		
		try {
			this.savedSearchService.delete(ss.getId());
			flash = "success|Saved search successfully deleted";
		}
		catch (Exception e) {
			flash = "failure|Failed to delete saved search";
		}
		
		return new RedirectView(String.format("%s/search/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}
	
	// Execute a saved search
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	public String get(@PathVariable int id, ModelMap model) {
		return get(id, 1, model);
	}
	
	// Execute an ad-hoc search
	@RequestMapping(value="/get/adhoc", method=RequestMethod.GET)
	public String getAdhoc(ModelMap model) {
		return get(ADHOC_ID, 1, model);
	}
	
	// Execute the search
	@RequestMapping(value="/get/{id}/{page}", method=RequestMethod.GET)
	public String get(@PathVariable int id, @PathVariable int page, ModelMap model) {
		SavedSearch ss = this.savedSearchService.get(id);
		SolrParams params = toSolrParams(ss.getJson()).setPageNum(page);		
		model.addAttribute(SAVED_SEARCH_ATTR, ss);			
		setCommonModelAttributes(ss, params, id == -1 ? ADHOC_MODE : UPDATE_MODE, model);		
		executeSearch(params, model);
		return RESULTS_VIEW;
	}	
}