package com.slepeweb.cms.bean.solr;

import java.util.List;

public class SolrResponse {

	private SolrParams params;
	private SolrConfig config;
	private List<SolrDocument> results;
	private long totalHits;
	private String message;
	private boolean error;
	private SolrPager pager;
	
	public SolrResponse(SolrParams p) {
		this.params = p;
	}
	
	public SolrParams getParams() {
		return params;
	}
	
	public void setParams(SolrParams params) {
		this.params = params;
	}
	
	public List<SolrDocument> getResults() {
		return results;
	}
	
	public void setResults(List<SolrDocument> results) {
		this.results = results;
	}
	
	public int getNumPages() {
		return (int) (1 + (getTotalHits()/this.params.getPageSize()));
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

	public SolrPager getPager() {
		return pager;
	}

	public void setPager(SolrPager pager) {
		this.pager = pager;
	}

	public SolrConfig getConfig() {
		return config;
	}

	public void setConfig(SolrConfig config) {
		this.config = config;
	}
}
