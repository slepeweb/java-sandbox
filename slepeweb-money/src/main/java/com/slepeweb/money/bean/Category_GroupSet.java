package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// TODO: Remove 'allMajors' from this list once you're sure all search json's have been updated

@JsonIgnoreProperties({"size", "firstGroup", "populated", "allMajors"})
public class Category_GroupSet {
	private String title = "Title";
	private List<Category_Group> groups = new ArrayList<Category_Group>();
	private String context;
	
	public Category_GroupSet() {}
	
	public Category_GroupSet(String ttl, String context) {
		this.title = ttl;
		this.context = context;
	}
	
	public boolean isPopulated() {
		for (Category_Group c : getGroups()) {
			if (c.isPopulated()) {
				return true;
			}
		}
		
		return false;
	}
	
	public void addGroup(Category_Group cg) {
		this.groups.add(cg);
		cg.setRoot(this);
	}
	
	public int getSize() {
		return this.groups.size();
	}
	
	public Category_Group getFirstGroup() {
		if (getSize() > 0) {
			return this.groups.get(0);
		}
		return null;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Category_GroupSet setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public List<Category_Group> getGroups() {
		return groups;
	}
	
	public Category_GroupSet setGroups(List<Category_Group> groups) {
		this.groups = groups;
		return this;
	}

	public String getContext() {
		return context;
	}

	public Category_GroupSet setContext(String context) {
		this.context = context;
		return this;
	}
}
