package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.slepeweb.cms.bean.Item;

public class Header implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<String> stylesheets, javascripts;
	private List<LinkTarget> globalNavigation, topNavigation;
	private List<Item> breadcrumbItems;
	private Page page;
	
	public Header(Page page) {
		this.stylesheets = new ArrayList<String>();
		this.javascripts = new ArrayList<String>();
		this.globalNavigation = new ArrayList<LinkTarget>();
		this.topNavigation = new ArrayList<LinkTarget>();
		this.breadcrumbItems = new ArrayList<Item>();
		this.page = page;
	}
	
	public void setBreadcrumbs(Item i) {
		this.breadcrumbItems = new ArrayList<Item>();
		
		while (! i.getPath().equals("/")) {
			this.breadcrumbItems.add(i);
			i = i.getParent();
		}
		
		// Lastly, add the root item
		this.breadcrumbItems.add(i);
		Collections.reverse(this.breadcrumbItems);
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
		List<LinkTarget> breadcrumbs = new ArrayList<LinkTarget>(getBreadcrumbItems().size());
		for (Item i : getBreadcrumbItems()) {
			breadcrumbs.add(new LinkTarget(i));
		}
		return breadcrumbs;
	}
	
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public List<Item> getBreadcrumbItems() {
		return breadcrumbItems;
	}
}
