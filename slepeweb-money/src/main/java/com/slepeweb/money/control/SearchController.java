package com.slepeweb.money.control;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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
	
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String list(ModelMap model) {
		model.addAttribute(SEARCH_LIST_ATTR, filterSavedSearches(ADVANCED_TYPE));
		return LIST_VIEW;
	}
	
	// Empty search definition form, for adding a new search
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public String create(ModelMap model) {
		
		model.addAttribute(ALL_ACCOUNTS_ATTR, this.accountService.getAll(true));
		model.addAttribute(ALL_PAYEES_ATTR, getAllPayees());
		model.addAttribute(CategoryController.ALL_MAJOR_CATEGORIES_ATTR, this.categoryService.getAllMajorValues());
		
		// Create a single, default category group
		model.addAttribute(CATEGORY_GROUP_ATTR, emptyCategoryGroup());		
		model.addAttribute(FORM_MODE_ATTR, CREATE_MODE);
		model.addAttribute(PARAMS_ATTR, new SolrParams(new SolrConfig()));
		return FORM_VIEW;
	}
	
	private CategoryGroup emptyCategoryGroup() {
		CategoryGroup grp = new CategoryGroup().setId(1).setLabel("unset");
		grp.getCategories().add(new CategoryInput().setId(1));
		return grp;
	}
	
	// Execute the search using form data, for a specific page.
	// Also save the search.
	@RequestMapping(value="/post/{id}", method=RequestMethod.POST)
	public String post(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		
		SolrParams params = getSearchCriteriaFromRequest(req).setPageNum(1);
		SavedSearch ss = this.savedSearchService.get(id);
		params.setCategories(getCategoriesFromRequest(req));
				
		ss.
				setType(ADVANCED_TYPE).
				setName(req.getParameter("name")).
				setJson(toJson(params)).
				setSaved(new Timestamp(new Date().getTime()));
		
		saveSearch(ss);
		setCommonModelAttributesAndSearchIfExecuteMode(ss, params, EXECUTE_MODE, model);		
		return RESULTS_VIEW;
	}	
	
	private void setCommonModelAttributesAndSearchIfExecuteMode(
			SavedSearch ss, SolrParams params, String formMode, ModelMap model) {
		
		model.addAttribute(SAVED_SEARCH_ATTR, ss);		
		model.addAttribute(PARAMS_ATTR, params);		
		model.addAttribute(CATEGORY_GROUP_ATTR, getCategoryGroup(params.getCategories()));				
		model.addAttribute(ALL_ACCOUNTS_ATTR, this.accountService.getAll(true));
		model.addAttribute(ALL_PAYEES_ATTR, getAllPayees());
		model.addAttribute(JSON_ATTR, Util.encodeUrl(toJson(params)));		
		model.addAttribute(FORM_MODE_ATTR, formMode);

		if (formMode.equals(EXECUTE_MODE)) {
			SolrResponse<FlatTransaction> resp = this.solrService.query(params);
			model.addAttribute(SEARCH_RESPONSE_ATTR, resp);		
			long credit = 0;
			for (FlatTransaction ft : resp.getResults()) {
				credit += ft.getAmount();
			}
			
			model.addAttribute("_totalCredit", credit);		
		}
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
			setPageSize(req.getParameter("pageSize")).
			setPageNum(1);
	}
	
	private List<Category> getCategoriesFromRequest(HttpServletRequest req) {
		String countersJson = req.getParameter("counterStore");
		List<MultiCategoryCounter> counters = fromJson(new TypeReference<List<MultiCategoryCounter>>() {}, countersJson);
		int groupId = 1;
		List<CategoryInput> inputs = readMultiCategoryInput(req, groupId, getNumCategoriesForGroup(counters, groupId));
		CategoryGroup grp = new CategoryGroup().setId(1).setCategories(inputs);
		return grp.toCategoryList();
	}
	
	// Save a newly created search
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public RedirectView save(HttpServletRequest req, ModelMap model) {
		
		SolrParams params = getSearchCriteriaFromRequest(req);
		params.setCategories(getCategoriesFromRequest(req));
				
		SavedSearch ss = new SavedSearch().
				setType(ADVANCED_TYPE).
				setName(req.getParameter("name")).
				setJson(toJson(params)).
				setSaved(new Timestamp(new Date().getTime()));
		
		String flash = saveSearch(ss);

		return new RedirectView(String.format("%s/search/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}	
	
	// Form to edit an exisiting search
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		
		SavedSearch ss = this.savedSearchService.get(id);
		SolrParams params = toSolrParams(ss.getJson());
		
		model.addAttribute("_numDeletableTransactions", 0);
		model.addAttribute(CategoryController.ALL_MAJOR_CATEGORIES_ATTR, this.categoryService.getAllMajorValues());
		setCommonModelAttributesAndSearchIfExecuteMode(ss, params, UPDATE_MODE, model);		
		return FORM_VIEW;
	}
	
	// Update an existing search on form submission
	@RequestMapping(value="/update/{id}", method=RequestMethod.POST)
	public RedirectView update(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		SolrParams params = getSearchCriteriaFromRequest(req);
		params.setCategories(getCategoriesFromRequest(req));

		SavedSearch ss = 
				this.savedSearchService.get(id).
				setName(req.getParameter("name")).
				setJson(toJson(params));
		
		String flash = saveSearch(ss);	
		
		return new RedirectView(String.format("%s/search/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
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
	public String get(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		return get(id, 1, req, model);
	}
	
	// Execute the search
	@RequestMapping(value="/get/{id}/{page}", method=RequestMethod.GET)
	public String get(@PathVariable int id, @PathVariable int page, HttpServletRequest req, ModelMap model) {
		
		SavedSearch ss = this.savedSearchService.get(id);
		SolrParams params = toSolrParams(ss.getJson()).setPageNum(page);
		
		model.addAttribute(SAVED_SEARCH_ATTR, ss);		
		setCommonModelAttributesAndSearchIfExecuteMode(ss, params, EXECUTE_MODE, model);		
		return RESULTS_VIEW;
	}	
	
	private CategoryGroup getCategoryGroup(List<Category> categories) {
		CategoryGroup grp = new CategoryGroup().setId(1);
		CategoryInput ci;
		
		if (categories.size() > 0) {
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
	
}