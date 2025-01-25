package com.slepeweb.money.control;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Category_GroupSet;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SavedSearchSupport;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;
import com.slepeweb.money.component.FormSupport;
import com.slepeweb.money.component.SearchFormSupport;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/search")
public class SearchController extends BaseController {
		
	private static final int ADHOC_ID = -1;
	
	@Autowired private FormSupport formSupport;
	@Autowired private SearchFormSupport searchFormSupport;
	
	/*
	 * 'Charts' and 'searches' use solr to query data, so the solr
	 * index needs to be up to date for results to be accurate.
	 */
	
	private SavedSearch setSavedSearch(SavedSearch ss, String name, String description, SolrParams params) {
		return ss.
			setType(SearchFormSupport.ADVANCED_TYPE).
			setName(name).
			setDescription(description).
			setJson(this.searchFormSupport.toJson(params)).
			setSaved(new Timestamp(new Date().getTime()));
	}

	private void executeSearch(SolrParams params, ModelMap model) {
		SolrResponse<FlatTransaction> resp = this.solrService.query(params);
		model.addAttribute(SearchFormSupport.SEARCH_RESPONSE_ATTR, resp);		
		long credit = 0;
		for (FlatTransaction ft : resp.getResults()) {
			credit += ft.getAmount();
		}
		
		model.addAttribute("_totalCredit", credit);		
	}
	
	private SavedSearchSupport processFormSubmission(HttpServletRequest req, SavedSearch ss) {
		Category_GroupSet cgs = this.formSupport.readCategoryInputs(req, 1);
		cgs.setContext(Category_GroupSet.SEARCH_CTX);
		SolrParams params = this.searchFormSupport.readSearchCriteria(req);
		params.setCategories(cgs);
				
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
		model.addAttribute(SearchFormSupport.SEARCH_LIST_ATTR, filterSavedSearches(SearchFormSupport.ADVANCED_TYPE));
		return SearchFormSupport.LIST_VIEW;
	}

	// Empty search definition form, for adding a new search
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public String create(ModelMap model) {
		this.searchFormSupport.populateForm(null, new SolrParams(new SolrConfig()), CREATE_MODE, model);		
		return SearchFormSupport.FORM_VIEW;
	}

	// Empty search definition form, for adding a new search
	@RequestMapping(value="/adhoc", method=RequestMethod.GET)
	public String adhoc(ModelMap model) {
		this.searchFormSupport.populateForm(null, null, ADHOC_MODE, model);		
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
		SolrParams params = this.searchFormSupport.fromJson(new TypeReference<SolrParams>() {}, ss.getJson());
		
		model.addAttribute("_numDeletableTransactions", 0);
		this.searchFormSupport.populateForm(ss, params, UPDATE_MODE, model);	
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
		return get(ADHOC_ID, 1, model);
	}
	
	// Execute the search
	@RequestMapping(value="/get/{id}/{page}", method=RequestMethod.GET)
	public String get(@PathVariable int id, @PathVariable int page, ModelMap model) {
		SavedSearch ss = this.savedSearchService.get(id);
		SolrParams params = this.searchFormSupport.fromJson(new TypeReference<SolrParams>() {}, ss.getJson());
		model.addAttribute(SAVED_SEARCH_ATTR, ss);			
		this.searchFormSupport.populateForm(ss, params, id == -1 ? ADHOC_MODE : UPDATE_MODE, model);		
		executeSearch(params, model);
		return SearchFormSupport.RESULTS_VIEW;
	}	
}