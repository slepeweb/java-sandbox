package com.slepeweb.money.bean;

import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.ModelMap;
import com.slepeweb.money.bean.solr.SolrParams;

public class SavedSearchSupport {
	private SavedSearch savedSearch;
	private SolrParams solrParams;
	private String flash;
	
	// TODO: Remove properties if not required
	private HttpServletRequest request;
	private ModelMap model;
	
	public SavedSearch getSavedSearch() {
		return savedSearch;
	}
	
	public SavedSearchSupport setSavedSearch(SavedSearch savedSearch) {
		this.savedSearch = savedSearch;
		return this;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}
	
	public SavedSearchSupport setRequest(HttpServletRequest request) {
		this.request = request;
		return this;
	}
	
	public ModelMap getModel() {
		return model;
	}
	
	public SavedSearchSupport setModel(ModelMap model) {
		this.model = model;
		return this;
	}
	
	public SolrParams getSolrParams() {
		return solrParams;
	}
	
	public SavedSearchSupport setSolrParams(SolrParams solrParams) {
		this.solrParams = solrParams;
		return this;
	}

	public String getFlash() {
		return flash;
	}

	public SavedSearchSupport setFlash(String s) {
		this.flash = s;
		return this;
	}
}
