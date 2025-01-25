package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"size"})
public class Category_GroupSet {
	public static final String TRANSACTION_CTX = "transaction";
	public static final String SEARCH_CTX = "search";
	public static final String CHART_CTX = "chart";

	private String title = "Title";
	private List<String> allMajors = new ArrayList<String>();
	private Map<String,List<String>> options = new HashMap<String,List<String>>();
	private List<Category_Group> groups = new ArrayList<Category_Group>();
	private String context;
	
	public Category_GroupSet() {}
	
	public Category_GroupSet(String ttl, String context, List<String> majors) {
		this.title = ttl;
		this.context = context;
		this.allMajors = majors;
	}
	
	public void addOptions(String major, List<String> allMinors) {
		if (! this.options.containsKey(major)) {
			this.options.put(major, allMinors);
		}
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
	
	public Map<String, List<String>> getOptions() {
		return options;
	}
	
	public Category_GroupSet setOptions(Map<String, List<String>> options) {
		this.options = options;
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
