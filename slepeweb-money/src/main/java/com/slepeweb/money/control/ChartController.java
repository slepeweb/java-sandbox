package com.slepeweb.money.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.slepeweb.common.util.JsonUtil;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.ChartProperties;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SavedSearchSupport;
import com.slepeweb.money.component.ChartFormSupport;
import com.slepeweb.money.component.SearchFormSupport;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/chart")
public class ChartController extends BaseController {
		
	@Autowired private SearchFormSupport searchFormSupport;
	@Autowired private ChartFormSupport chartFormSupport;
	
	/*
	 * 'Charts' and 'searches' use solr to query data, so the solr
	 * index needs to be up to date for results to be accurate.
	 */
	
	// Empty chart definition form, for adding a new chart
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String create(HttpServletRequest req, ModelMap model) {
		this.chartFormSupport.populateForm(null, new ChartProperties(), SearchFormSupport.CREATE_MODE, model);
		model.addAttribute(ALL_MAJORS, this.categoryService.getAllMajorValues());
		return ChartFormSupport.FORM_VIEW;
	}
	
	// Form to update an existing chart
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		
		SavedSearch ss = this.savedSearchService.get(id);
		ChartProperties props = JsonUtil.fromJson(new TypeReference<ChartProperties>() {}, ss.getJson());
		this.chartFormSupport.convertId2Name(props);

		model.addAttribute("_numDeletableTransactions", 0);		
		this.chartFormSupport.populateForm(ss, props, SearchFormSupport.UPDATE_MODE, model);
		model.addAttribute(ALL_MAJORS, this.categoryService.getAllMajorValues());
		return ChartFormSupport.FORM_VIEW;
	}
	
	// Handle form submission for updating an existing chart
	@RequestMapping(value="/save/{id}", method=RequestMethod.POST)
	public RedirectView save(@PathVariable int id, HttpServletRequest req, ModelMap model) {

		return save(req, this.savedSearchService.get(id));
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public RedirectView delete(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		SavedSearch ss = this.savedSearchService.get(id);
		String flash;
		
		try {
			this.savedSearchService.delete(ss.getId());
			flash = "success|Chart successfully deleted";
		}
		catch (Exception e) {
			flash = "failure|Failed to delete chart";
		}
		
		return new RedirectView(String.format("%s/chart/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}
	
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String list(ModelMap model) {
		model.addAttribute("_charts", filterSavedSearches("chart"));
		return ChartFormSupport.LIST_VIEW;
	}
	
	// Handle form submission for creating a new chart
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public RedirectView save(HttpServletRequest req, ModelMap model) {

		return save(req, new SavedSearch().setType(SearchFormSupport.CHART_CTX));
	}	
	
	private RedirectView save(HttpServletRequest req, SavedSearch ss) {
		
		SavedSearchSupport supp = this.chartFormSupport.processFormSubmission(req, ss);
		
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
		
		return this.searchFormSupport.redirect2List(supp);
	}	
	
	// Produces a chart from a GET request (ie a link)
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	public String get(@PathVariable int id, HttpServletRequest req, ModelMap model) {

		SavedSearch ss = this.savedSearchService.get(id);
		ChartProperties props = JsonUtil.fromJson(new TypeReference<ChartProperties>() {}, ss.getJson());
		this.chartFormSupport.convertId2Name(props);
		this.chartFormSupport.populateForm(ss, props, SearchFormSupport.EXECUTE_MODE, model);

		model.addAttribute(SearchFormSupport.FORM_MODE_ATTR, SearchFormSupport.EXECUTE_MODE);
		model.addAttribute(SearchFormSupport.SAVED_SEARCH_ATTR, ss);
		return this.chartFormSupport.executeSearches(props, req, model);
	}	
			
}