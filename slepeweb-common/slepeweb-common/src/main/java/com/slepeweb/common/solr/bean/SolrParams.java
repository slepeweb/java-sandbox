package com.slepeweb.common.solr.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.slepeweb.common.util.NumberUtil;

@JsonIgnoreProperties({"start", "hrefBase"})
public class SolrParams {

	private SolrConfig config;
	private int pageNum, pageSize;
	
	/*
	 * This object should always be a User object, as defined in the cms-core project
	 */
	private Object user; 
	
	// For Jackson
	public SolrParams() {}
	
	public SolrParams(SolrConfig config) {
		this.config = config;
	}
	
	public int getPageSize() {
		if (this.pageSize == 0) {
			this.pageSize = this.config.getPageSize();
		}
		return this.pageSize;
	}

	@JsonSetter("pageSize")
	public SolrParams setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public SolrParams setPageSize(String s) {
		this.pageSize = NumberUtil.toInteger(s, this.config.getPageSize());
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

	@JsonSetter("pageNum")
	public SolrParams setPageNum(int pageNum) {
		this.pageNum = pageNum;
		return this;
	}

	public SolrParams setPageNum(String s) {
		this.pageNum = NumberUtil.toInteger(s, 1);
		return this;
	}

	public SolrConfig getConfig() {
		return config;
	}

	public Object getUser() {
		return user;
	}

	public void setUser(Object user) {
		this.user = user;
	}

}
