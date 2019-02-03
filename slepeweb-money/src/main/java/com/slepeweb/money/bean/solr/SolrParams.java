package com.slepeweb.money.bean.solr;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

public class SolrParams {

	private SolrConfig config;
	private String memo, majorCategory, payeeName;
	private Long accountId, payeeId, categoryId;
	private int pageNum, pageSize;

	public SolrParams(SolrConfig config) {
		this.config = config;
	}
	
	public String getUrlParameters() {
		StringBuilder sb = new StringBuilder();
		if (getAccountId() != null) {
			appendParam(sb, "accountId", getAccountId());
		}
		
		if (getPayeeId() != null) {
			appendParam(sb, "payeeId", getPayeeId());
		}
		
		if (getCategoryId() != null) {
			appendParam(sb, "categoryId", getCategoryId());
		}
		
		if (StringUtils.isNotBlank(getMajorCategory())) {
			appendParam(sb, "category", getMajorCategory());
		}
		
		if (StringUtils.isNotBlank(getMemo())) {
			appendParam(sb, "memo", getMemo());
		}
		
		return sb.toString();
	}
	
	private void appendParam(StringBuilder sb, String fieldName, Long fieldValue) {
		appendParam(sb, fieldName, String.valueOf(fieldValue));
	}
	
	private void appendParam(StringBuilder sb, String fieldName, String fieldValue) {
		if (sb.length() > 0) {
			sb.append("&");
		}
		sb.append(fieldName).append("=").append(clean(fieldValue));
	}
	
	public String getPayeeName() {
		return payeeName;
	}

	public SolrParams setPayeeName(String payeeName) {
		if (StringUtils.isNotBlank(payeeName)) {
			this.payeeName = payeeName;
		}
		return this;
	}

	public String getMemo() {
		return this.memo == null ? "" : this.memo;
	}

	public SolrParams setMemo(String memo) {
		this.memo = memo;
		return this;
	}

	public String getMajorCategory() {
		return this.majorCategory == null ? "" : this.majorCategory;
	}

	public SolrParams setMajorCategory(String majorCategory) {
		this.majorCategory = majorCategory;
		return this;
	}

	public Long getPayeeId() {
		return payeeId;
	}

	public String getPayeeIdStr() {
		return getPayeeId() == null ? "" : String.valueOf(getPayeeId());
	}

	public SolrParams setPayeeId(Long payeeId) {
		this.payeeId = payeeId;
		return this;
	}

	public SolrParams setPayeeId(String payeeId) {
		if (StringUtils.isNumeric(payeeId)) {
			this.payeeId = Long.valueOf(payeeId);
		}
		else {
			this.payeeId = null;
		}
		
		return this;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public String getCategoryIdStr() {
		return getCategoryId() == null ? "" : String.valueOf(getCategoryId());
	}

	public SolrParams setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public SolrParams setCategoryId(String categoryId) {
		if (StringUtils.isNumeric(categoryId)) {
			this.categoryId = Long.valueOf(categoryId);
		}
		else {
			this.categoryId = null;
		}
		
		return this;
	}

	public Long getAccountId() {
		return accountId;
	}

	public String getAccountIdStr() {
		return getAccountId() == null ? "" : String.valueOf(getAccountId());
	}

	public SolrParams setAccountId(Long accountId) {
		this.accountId = accountId;
		return this;
	}

	public SolrParams setAccountId(String accountId) {
		if (StringUtils.isNumeric(accountId)) {
			this.accountId = Long.valueOf(accountId);
		}
		else {
			this.accountId = null;
		}
		
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
