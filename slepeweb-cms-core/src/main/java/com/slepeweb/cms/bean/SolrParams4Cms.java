package com.slepeweb.cms.bean;

import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.common.solr.bean.SolrParams;

public class SolrParams4Cms extends SolrParams {

	private String searchText, language;
	private Long siteId;

	public SolrParams4Cms(SolrConfig config) {
		super(config);
	}
	
	public String getSearchText() {
		return searchText;
	}

	public SolrParams4Cms setSearchText(String searchText) {
		this.searchText = searchText;
		return this;
	}

	public Long getSiteId() {
		return siteId;
	}

	public SolrParams4Cms setSiteId(Long siteId) {
		this.siteId = siteId;
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public SolrParams4Cms setLanguage(String language) {
		this.language = language;
		return this;
	}
}
