package com.slepeweb.cms.bean.solr;

public class SolrConfig {

	public static final String PAGE_SIZE_KEY = "solr.page.size";
	public static final String MAX_PAGES_KEY = "solr.max.pages";
	
	private static final Integer DFLT_PAGE_SIZE = 5;
	private static final Integer DFLT_MAX_PAGES = 5;
	private Integer pageSize, maxPages;

	public Integer getPageSize() {
		if (this.pageSize == null) {
			return DFLT_PAGE_SIZE;
		}
		return this.pageSize;
	}

	public SolrConfig setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public Integer getMaxPages() {
		if (this.maxPages == null) {
			return DFLT_MAX_PAGES;
		}
		return this.maxPages;
	}

	public SolrConfig setMaxPages(Integer maxPages) {
		this.maxPages = maxPages;
		return this;
	}
}
