package com.slepeweb.cms.bean.solr;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;

public class SolrParams {

	private SolrConfig config;
	private String searchText;
	private Item searchResultsItem;
	private int pageNum, pageSize;

	public SolrParams(Item i, SolrConfig config) {
		this.searchResultsItem = i;
		this.config = config;
	}
	
	public String getSearchText() {
		return searchText;
	}

	public SolrParams setSearchText(String searchText) {
		this.searchText = searchText;
		return this;
	}

	public int getPageSize() {
		if (this.pageSize == 0) {
			this.pageSize = this.config.getPageSize();
		}
		return this.pageSize;
	}

	public SolrParams setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public int getPageNum() {
		if ( this.pageNum == 0) {
			this.pageNum = 1;
		}
		return this.pageNum;
	}
	
	public int getStart() {
		return (getPageNum() - 1) * getPageSize();
	}

	public SolrParams setPageNum(int pageNum) {
		this.pageNum = pageNum;
		return this;
	}

	public SolrParams setPageNum(String s) {
		if (StringUtils.isNumeric(s)) {
			this.pageNum = Integer.parseInt(s);
		}
		else {
			this.pageNum = 1;
		}
		return this;
	}

	public Item getSearchResultsItem() {
		return searchResultsItem;
	}

	public SolrParams setSearchResultsItem(Item searchResultsItem) {
		this.searchResultsItem = searchResultsItem;
		return this;
	}
	
	public Long getSiteId() {
		return this.searchResultsItem.getSite().getId();
	}
	
	public String getHrefBase() {
		StringBuilder sb = new StringBuilder(this.searchResultsItem.getPath());
		sb.append("?").append("searchText=").append(clean(getSearchText()));
		return sb.toString();
	}
	
	private String clean(String s) {
		String cleaned = s.replaceAll("[<>]", " ");
		try {
			return URLEncoder.encode(cleaned, "utf-8");
		}
		catch (UnsupportedEncodingException e) {}
		return cleaned;
	}

	public SolrConfig getConfig() {
		return config;
	}
}
