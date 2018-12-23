package com.slepeweb.money.bean.solr;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

public class SolrParams {

	private SolrConfig config;
	private String memo;
	private Long payeeId, categoryId;
	private int pageNum, pageSize;

	public SolrParams(SolrConfig config) {
		this.config = config;
	}
	
	public String getMemo() {
		return memo;
	}

	public SolrParams setMemo(String memo) {
		this.memo = memo;
		return this;
	}

	public Long getPayeeId() {
		return payeeId;
	}

	public SolrParams setPayeeId(Long payeeId) {
		this.payeeId = payeeId;
		return this;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public SolrParams setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
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

	public String getHrefBase() {
		StringBuilder sb = new StringBuilder("/search");
		//sb.append("?").append("searchText=").append(clean(getSearchText()));
		return sb.toString();
	}
	
	@SuppressWarnings("unused")
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
