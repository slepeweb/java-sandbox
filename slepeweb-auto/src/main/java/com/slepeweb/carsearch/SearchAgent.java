package com.slepeweb.carsearch;

import org.apache.commons.lang3.StringUtils;

public class SearchAgent {
	private SearchCriteria criteria;
	private SearchResults results, previous;
	private String serializedFilePrefix;
	
	public SearchAgent() {}
	
	public SearchAgent(String heading, String trimFilter, String url, String filePrefix) {
		this.criteria = new SearchCriteria(heading, trimFilter, url);
		this.results = new SearchResults();
		this.previous = new SearchResults();
		this.serializedFilePrefix = filePrefix;
	}	
	
	/*
	 * Title must match ALL filters
	 */
	public boolean titleMatchesTrimFilter(String title) {
		if (StringUtils.isNotBlank(title) && getCriteria().getTrimRegex() != null) {
			String regex;
			for (String filter : getCriteria().getTrimRegex()) {
				regex = "^.*?" + filter.toLowerCase().trim() + ".*$";
				if (! title.toLowerCase().matches(regex)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public SearchCriteria getCriteria() {
		return criteria;
	}
	
	public void setCriteria(SearchCriteria criteria) {
		this.criteria = criteria;
	}
	
	public SearchResults getResults() {
		return results;
	}
	
	public void setResults(SearchResults results) {
		this.results = results;
	}

	public String getSerializedFilePrefix() {
		return serializedFilePrefix;
	}

	public void setSerializedFilePrefix(String serializedFilePrefix) {
		this.serializedFilePrefix = serializedFilePrefix;
	}

	public SearchResults getPrevious() {
		return previous;
	}

	public void setPrevious(SearchResults previous) {
		this.previous = previous;
	}
}
