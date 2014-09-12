package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sidebar implements NestableComponent, Serializable {
	private static final long serialVersionUID = 1L;
	private List<LinkTarget> navigation, relatedPages;
	private List<SimpleComponent> components;
	
	public Sidebar() {
		this.navigation = new ArrayList<LinkTarget>();
		this.relatedPages = new ArrayList<LinkTarget>();
		this.components = new ArrayList<SimpleComponent>();
	}
	
	public List<SimpleComponent> getComponents() {
		return components;
	}
	
	public List<LinkTarget> getNavigation() {
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

	public void setComponents(List<SimpleComponent> components) {
		this.components = components;
	}
}
