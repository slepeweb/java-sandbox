package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"size"})
public class Category_GroupSet {
	private String title = "Title";
	private List<String> allMajors = new ArrayList<String>();
	private List<Category_Group> groups = new ArrayList<Category_Group>();
	private String context;
	
	public Category_GroupSet() {}
	
	public Category_GroupSet(String ttl, String context, List<String> majors) {
		this.title = ttl;
		this.context = context;
		this.allMajors = majors;
	}
	
	public void addGroup(Category_Group cg) {
		this.groups.add(cg);
		cg.setRoot(this);
	}
	
	public int getSize() {
		return this.groups.size();
	}
	
	public String getTitle() {
		return title;
	}
	
	public Category_GroupSet setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public List<String> getAllMajors() {
		return allMajors;
	}
	
	public Category_GroupSet setAllMajors(List<String> allMajors) {
		this.allMajors = allMajors;
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
