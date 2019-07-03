package com.slepeweb.money.control;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.slepeweb.money.bean.MultiCategoryCounter;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;

@Controller
@RequestMapping(value="/search")
public class SearchController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(SearchController.class);
	
	private static final String CATEGORY_GROUP_ATTR = "_categoryGroup";
	private static final String SEARCH_RESPONSE_ATTR = "_response";
	private static final String TYPE_ADVANCED = "advanced";
	private static final String ALL_ACCOUNTS_ATTR = "_allAccounts";
	private static final String ALL_PAYEES_ATTR = "_allPayees";
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String list(ModelMap model) {
		
		List<SavedSearch> all = this.savedSearchService.getAll();
		List<SavedSearch> searches = new ArrayList<SavedSearch>();
		
		for (SavedSearch ss : all) {
			if (ss.getType().equals("advanced")) {			
				searches.add(ss);
			}
		}
		
		model.addAttribute("_searches", searches);
		return "searchList";
	}
	
	// Empty search definition form, for adding a new search
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public String create(ModelMap model) {
		
		model.addAttribute("_allAccounts", this.accountService.getAll(true));
		model.addAttribute("_allPayees", getAllPayees());
		
		// Create a single, default category group
		CategoryGroup grp = new CategoryGroup().setId(1).setLabel("unset");
		grp.getCategories().add(new CategoryInput().setId(1));
		model.addAttribute(CATEGORY_GROUP_ATTR, grp);
		
		model.addAttribute("_formMode", "create");
		return "searchForm";
	}
	
	// Execute the search using form data
	@RequestMapping(value="/action/{id}", method=RequestMethod.POST)
	public String action(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		return actionPage(id, 1, req, model);
	}
	
	// Execute the search using form data, for a specific page
	@RequestMapping(value="/action/{id}/{page}", method=RequestMethod.POST)
	public String actionPage(@PathVariable int id, @PathVariable int page, HttpServletRequest req, ModelMap model) {
		
		SolrParams params = getSearchCriteriaFromRequest(req).setPageNum(page);
		SavedSearch ss = this.savedSearchService.get(id);
		params.setCategories(getCategoriesFromRequest(req));
				
		ss.
				setType(TYPE_ADVANCED).
				setName(req.getParameter("name")).
				setJson(toJson(params)).
				setSaved(new Timestamp(new Date().getTime()));
		
		try {
			this.savedSearchService.save(ss);
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to save search [%s]", ss));
		}
				
		model.addAttribute("_ss", ss);		
		model.addAttribute("_params", params);		
		model.addAttribute(SEARCH_RESPONSE_ATTR, this.solrService.query(params));				
		model.addAttribute(CATEGORY_GROUP_ATTR, getCategoryGroup(params.getCategories()));				
		model.addAttribute(ALL_ACCOUNTS_ATTR, this.accountService.getAll(true));
		model.addAttribute(ALL_PAYEES_ATTR, getAllPayees());
		model.addAttribute("_formMode", "execute");
		
		return "searchResults";
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
				setType(TYPE_ADVANCED).
				setName(req.getParameter("name")).
				setJson(toJson(params)).
				setSaved(new Timestamp(new Date().getTime()));
		
		String flash;
		
		try {
			this.savedSearchService.save(ss);
			flash = "success|Search successfully saved";
		}
		catch (Exception e) {
			flash = "failure|Failed to save search";
		}

		return new RedirectView(String.format("%s/search/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}	
	
	// Form to edit an exisiting search
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		
		SavedSearch ss = this.savedSearchService.get(id);
		SolrParams params = toSolrParams(ss.getJson());
		
		model.addAttribute("_ss", ss);
		model.addAttribute("_params", params);
		model.addAttribute("_numDeletableTransactions", 0);
		model.addAttribute("_allAccounts", this.accountService.getAll(true));
		model.addAttribute("_allPayees", getAllPayees());
		model.addAttribute(CATEGORY_GROUP_ATTR, getCategoryGroup(params.getCategories()));				

		model.addAttribute("_formMode", "update");
		return "searchForm";
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
		
		String flash;
		
		try {
			this.savedSearchService.save(ss);
			flash = "success|Search successfully updated";
		}
		catch (Exception e) {
			flash = "failure|Failed to update search";
		}
		
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
		
		String jsonStr = req.getParameter("json");
		SolrParams params = toSolrParams(jsonStr);
		
		model.addAttribute("_ss", this.savedSearchService.get(id));		
		model.addAttribute("_params", params);		
		model.addAttribute(SEARCH_RESPONSE_ATTR, this.solrService.query(params));		
		model.addAttribute(CATEGORY_GROUP_ATTR, getCategoryGroup(params.getCategories()));				
		model.addAttribute(ALL_ACCOUNTS_ATTR, this.accountService.getAll(true));
		model.addAttribute(ALL_PAYEES_ATTR, getAllPayees());
		model.addAttribute("_formMode", "execute");

		return "searchResults";
	}	
	
	private CategoryGroup getCategoryGroup(List<Category> categories) {
		CategoryGroup grp = new CategoryGroup().setId(1);
		CategoryInput ci;
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
	
	private static SolrParams toSolrParams(String jsonPacket) {

		try {
			return new ObjectMapper().readValue(jsonPacket, SolrParams.class);
		} catch (Exception e) {
			LOG.error("Jackson marshalling error", e);
		}
		return null;
	}
	
}