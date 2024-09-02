package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.site.service.NavigationService;

public class Header implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(Header.class);
	
	private List<String> stylesheets, javascripts;
	private List<LinkTarget> globalNavigation, topNavigation;
	private List<Item> breadcrumbItems;
	private Page page;
	
	public Header(Page page) {
		this.stylesheets = new ArrayList<String>();
		this.javascripts = new ArrayList<String>();
		this.page = page;
	}
	
	private void populateBreadcrumbs() {
		this.breadcrumbItems = new ArrayList<Item>();
		Item i = getPage().getItem();
		
		while (! i.getPath().equals("/")) {
			this.breadcrumbItems.add(i);
			i = i.getOrthogonalParent();
		}
		
		// Lastly, add the root item
		this.breadcrumbItems.add(i);
		Collections.reverse(this.breadcrumbItems);
	}
	
	private void populateTopNavigation(Item i) {
		this.topNavigation = new ArrayList<LinkTarget>();
		ItemService itemService = i.getCmsService().getItemService();
		NavigationService navigationService = getPage().getNavigationService();
		Item root = itemService.getItem(i.getSite().getId(), "/");
		
		if (root != null) {
			this.topNavigation.addAll(navigationService.drillDown(root, 3, i.getPath()).getChildren());
			LOG.debug(String.format("Top navigation has %d entries", this.topNavigation.size()));
		}
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
	
	public List<LinkTarget> getTopNavigation() {
		if (this.topNavigation == null) {
			populateTopNavigation(getPage().getItem());
		}
		return this.topNavigation;
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
		if (this.breadcrumbItems == null) {
			populateBreadcrumbs();
		}
		return breadcrumbItems;
	}
}
