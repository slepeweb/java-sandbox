package com.slepeweb.common.solr.bean;

import java.util.List;

public class SolrResponse<T> {

	private SolrConfig config;
	private List<T> results;
	private long totalHits;
	private String message;
	private boolean error;
	private SolrPager<T> pager;
	
	public List<T> getResults() {
		return results;
	}
	
	public void setResults(List<T> results) {
		this.results = results;
	}
	
	public int getNumPages() {
		return this.pager.getMaxPages();
	}
	
	public long getTotalHits() {
		return totalHits;
	}
	
	public void setTotalHits(long totalHits) {
		this.totalHits = totalHits;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public SolrPager<T> getPager() {
		return pager;
	}

	public void setPager(SolrPager<T> pager) {
		this.pager = pager;
	}

	public SolrConfig getConfig() {
		return config;
	}

	public void setConfig(SolrConfig config) {
		this.config = config;
	}
}
