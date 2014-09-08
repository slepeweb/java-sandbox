package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Header implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<String> stylesheets, javascripts;
	private List<LinkTarget> globalNavigation, topNavigation, breadcrumbs;
	private Page page;
	
	public Header(Page page) {
		this.stylesheets = new ArrayList<String>();
		this.javascripts = new ArrayList<String>();
		this.globalNavigation = new ArrayList<LinkTarget>();
		this.topNavigation = new ArrayList<LinkTarget>();
		this.breadcrumbs = new ArrayList<LinkTarget>();
		this.page = page;
	}
	
	public List<String> getStylesheets() {
		return stylesheets;
	}

	public List<String> getJavascripts() {
		return javascripts;
	}

	public List<LinkTarget> getGlobalNavigation() {
		return globalNavigation;
	}
	
	public void setGlobalNavigation(List<LinkTarget> globalNavigation) {
		this.globalNavigation = globalNavigation;
	}
	
	public List<LinkTarget> getTopNavigation() {
		return topNavigation;
	}
	
	public void setTopNavigation(List<LinkTarget> topNavigation) {
		this.topNavigation = topNavigation;
	}
	
	public List<LinkTarget> getBreadcrumbs() {
		return breadcrumbs;
	}
	
	public void setBreadcrumbs(List<LinkTarget> breadcrumbs) {
		this.breadcrumbs = breadcrumbs;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
