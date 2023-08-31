package com.slepeweb.site.bean;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.common.solr.bean.SolrParams;
import com.slepeweb.common.util.HttpUtil;

public class SolrParams4Site extends SolrParams {

	private String searchText;
	private Item searchResultsItem;

	public SolrParams4Site(Item i, SolrConfig config) {
		super(config);
		this.searchResultsItem = i;
		setUser(i.getUser());
	}
	
	public String getSearchText() {
		return searchText;
	}

	public SolrParams4Site setSearchText(String searchText) {
		this.searchText = searchText;
		return this;
	}

	public Item getSearchResultsItem() {
		return searchResultsItem;
	}

	public SolrParams4Site setSearchResultsItem(Item searchResultsItem) {
		this.searchResultsItem = searchResultsItem;
		return this;
	}
	
	public Long getSiteId() {
		return this.searchResultsItem.getSite().getId();
	}
	
	public String getHrefBase() {
		StringBuilder sb = new StringBuilder(this.searchResultsItem.getUrl());
		sb.append("?").append("text=").append(HttpUtil.clean(getSearchText()));
		return sb.toString();
	}
	
}
