package com.slepeweb.sandbox.www.model;

import java.util.ArrayList;
import java.util.List;

public class Header {
	private List<String> stylesheets, javascripts;
	private List<Link> globalNavigation, topNavigation, breadcrumbs;
	
	public Header() {
		this.stylesheets = new ArrayList<String>();
		this.javascripts = new ArrayList<String>();
		this.globalNavigation = new ArrayList<Link>();
		this.topNavigation = new ArrayList<Link>();
		this.breadcrumbs = new ArrayList<Link>();
	}
	
	public List<String> getStylesheets() {
		return stylesheets;
	}

	public List<String> getJavascripts() {
		return javascripts;
	}

	public List<Link> getGlobalNavigation() {
		return globalNavigation;
	}
	
	public void setGlobalNavigation(List<Link> globalNavigation) {
		this.globalNavigation = globalNavigation;
	}
	
	public List<Link> getTopNavigation() {
		return topNavigation;
	}
	
	public void setTopNavigation(List<Link> topNavigation) {
		this.topNavigation = topNavigation;
	}
	
	public List<Link> getBreadcrumbs() {
		return breadcrumbs;
	}
	
	public void setBreadcrumbs(List<Link> breadcrumbs) {
		this.breadcrumbs = breadcrumbs;
	}
}
