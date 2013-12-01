package com.slepeweb.sandbox.www.model;

import java.util.ArrayList;
import java.util.List;

public class Sidebar {
	private List<Link> navigation, relatedPages;
	private List<Component> components;
	
	public Sidebar() {
		this.navigation = new ArrayList<Link>();
		this.relatedPages = new ArrayList<Link>();
		this.components = new ArrayList<Component>();
	}
	
	public List<Component> getComponents() {
		return components;
	}
	
	public List<Link> getNavigation() {
		return navigation;
	}
	
	public List<Link> getRelatedPages() {
		return relatedPages;
	}

	public void setNavigation(List<Link> navigation) {
		this.navigation = navigation;
	}
}
