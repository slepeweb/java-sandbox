package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.site.constant.FieldName;

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
			i = i.getParent();
		}
		
		// Lastly, add the root item
		this.breadcrumbItems.add(i);
		Collections.reverse(this.breadcrumbItems);
	}
	
	private void populateTopNavigation(Item i) {
		this.topNavigation = new ArrayList<LinkTarget>();		
		Item root = i.getCmsService().getItemService().getItem(i.getSite().getId(), "/");
		
		if (root != null) {
			this.topNavigation.addAll(drillDown(root, 1, i.getPath()).getChildren());
			LOG.debug(String.format("Top navigation has %d entries", this.topNavigation.size()));
		}
	}
	
	private LinkTarget drillDown(Item parent, int numLevels, String currentItemPath) {
		LinkTarget parentTarget = createLinkTarget(parent, currentItemPath);
		
		if (parentTarget != null && numLevels-- > 0) {
			LinkTarget childTarget;
			
			if (! parent.getFieldValue(FieldName.HIDE_CHILDREN_FROM_NAV, "").equalsIgnoreCase("yes")) {
				for (Link l : parent.getBindings()) {
					childTarget = drillDown(l.getChild(), numLevels, currentItemPath);
					if (childTarget != null) {
						parentTarget.getChildren().add(childTarget);
					}
				}
			}
		}

		return parentTarget;
	}
	
	private LinkTarget createLinkTarget(Item child, String currentItemPath) {
		
		if (! child.getFieldValue(FieldName.HIDE_FROM_NAV, "").equalsIgnoreCase("yes")) {
			LinkTarget lt = new LinkTarget(child).
					setSelected(currentItemPath.startsWith(child.getPath()));
			return lt;
		}
		
		return null;
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
