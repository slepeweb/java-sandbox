package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.slepeweb.cms.bean.Item;

public class Sidebar implements NestableComponent, Serializable {
	private static final long serialVersionUID = 1L;
	private List<LinkTarget> navigation, relatedPages;
	private List<SimpleComponent> components;

	private Page page;
	private Sidebar.Type type;
	
	public enum Type {
		left, right;
	}
	
	public Sidebar(Page page, Sidebar.Type type) {
		this.page = page;
		this.type = type;
		this.components = new ArrayList<SimpleComponent>();
	}
	
	public void populateNavigation() {
		List<LinkTarget> nav = new ArrayList<LinkTarget>();
		
		if (this.page.getHeader().getBreadcrumbs().size() > 1) {
			Item levelOneItem = this.page.getHeader().getBreadcrumbItems().get(1);
			nav.add(this.page.getNavigationService().drillDown(levelOneItem, 3, this.page.getItem().getPath()));
		}
		
		setNavigation(nav);
	}
	
	public List<SimpleComponent> getComponents() {
		return components;
	}
	
	public void setComponents(List<SimpleComponent> components) {
		this.components = components;
	}
	
	public List<LinkTarget> getNavigation() {
		if (this.navigation == null) {
			populateNavigation();
		}
		return navigation;
	}
	
	public List<LinkTarget> getRelatedPages() {
		return relatedPages;
	}

	public void setNavigation(List<LinkTarget> navigation) {
		this.navigation = navigation;
	}

	public void setRelatedPages(List<LinkTarget> relatedPages) {
		this.relatedPages = relatedPages;
	}

	public Page getPage() {
		return page;
	}

	public Sidebar.Type getType() {
		return type;
	}

	public Sidebar setType(Sidebar.Type type) {
		this.type = type;
		return this;
	}
}
