package com.slepeweb.money.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SavedSearchSupport;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.component.SearchFormSupport;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/search")
public class SearchController extends BaseController {
		
	@Autowired private SearchFormSupport searchFormSupport;
	
	/*
	 * 'Charts' and 'searches' use solr to query data, so the solr
	 * index needs to be up to date for results to be accurate.
	 */
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String list(ModelMap model) {
		model.addAttribute(SearchFormSupport.SEARCH_LIST_ATTR, filterSavedSearches(SearchFormSupport.ADVANCED_TYPE));
		return SearchFormSupport.LIST_VIEW;
	}

	// Empty search definition form, for adding a new search
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public String create(ModelMap model) {
		this.searchFormSupport.populateForm(null, new SolrParams(new SolrConfig()), SearchFormSupport.CREATE_MODE, model);		
		return SearchFormSupport.FORM_VIEW;
	}

	/* 
	 * Empty search definition form, for adding a new ad-hoc search
	 * Ad-hoc searches require an entry in the search db table with id = -1. All ad-hoc searches use and
	 * update this single row in the table to record the search criteria.
	 * 
	 * TODO: As it currently stands, the app is a single-user app. That is, two users would be unable to
	 * execute ad-hoc searches at the same time.
	 */
	@RequestMapping(value="/adhoc", method=RequestMethod.GET)
	public String adhoc(ModelMap model) {
		this.searchFormSupport.populateForm(null, new SolrParams(new SolrConfig()), SearchFormSupport.ADHOC_MODE, model);		
		return SearchFormSupport.FORM_VIEW;
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
		
		SavedSearchSupport supp = this.searchFormSupport.processFormSubmission(req, ss);
		
		if (supp.isAdhoc()) {
			ss.setId(SavedSearch.ADHOC_ID);
			storeSavedSearch(ss);
			return this.searchFormSupport.redirect2Adhoc(supp.setFlash(""));
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
				return this.searchFormSupport.redirect2Execute(supp);
			}
		}
		
		return this.searchFormSupport.redirect2List(supp);
	}
	
	// Form to edit an existing search
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		
		SavedSearch ss = this.savedSearchService.get(id);
		SolrParams params = this.searchFormSupport.fromJson(new TypeReference<SolrParams>() {}, ss.getJson());
		
		model.addAttribute("_numDeletableTransactions", 0);
		this.searchFormSupport.populateForm(ss, params, SearchFormSupport.UPDATE_MODE, model);	
		return SearchFormSupport.FORM_VIEW;
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
		return get(SavedSearch.ADHOC_ID, 1, model);
	}
	
	// Execute the search
	@RequestMapping(value="/get/{id}/{page}", method=RequestMethod.GET)
	public String get(@PathVariable int id, @PathVariable int page, ModelMap model) {
		SavedSearch ss = this.savedSearchService.get(id);
		SolrParams params = this.searchFormSupport.fromJson(new TypeReference<SolrParams>() {}, ss.getJson());
		params.setPageNum(page);
		model.addAttribute(SearchFormSupport.SAVED_SEARCH_ATTR, ss);			
		this.searchFormSupport.populateForm(ss, params, id == -1 ? SearchFormSupport.ADHOC_MODE : SearchFormSupport.UPDATE_MODE, model);		
		this.searchFormSupport.executeSearch(params, model);
		return SearchFormSupport.RESULTS_VIEW;
	}	
}