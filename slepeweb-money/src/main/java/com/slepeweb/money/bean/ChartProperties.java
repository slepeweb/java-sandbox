package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * This class supports a combination of purposes:
 * a) supports rendition of forms (groups)
 * b) in json form, it can be stored in the db (inputGroups)
 */
@JsonIgnoreProperties({"ready", "inputGroups"})
public class ChartProperties {
/*
	private String title = "";
	private int fromYear = 2015, toYear = 2019;
	
	// This list is stored in the db as a json string
	private List<SearchCategoryGroup> groups = new ArrayList<SearchCategoryGroup>();
	
	// This list supports rendering the chart forms
	private List<SearchCategoryInputGroup> inputGroups = new ArrayList<SearchCategoryInputGroup>();
	
	public int getFromYear() {
		return fromYear;
	}
	
	public ChartProperties setFromYear(int fromYear) {
		this.fromYear = fromYear;
		return this;
	}
	
	public int getToYear() {
		return toYear;
	}
	
	public ChartProperties setToYear(int to) {
		this.toYear = to;
		return this;
	}

	public List<SearchCategoryGroup> getGroups() {
		return this.groups;
	}

	public ChartProperties setGroups(List<SearchCategoryGroup> groups) {
		this.groups = groups;
		return this;
	}
	
	public List<SearchCategoryInputGroup> getInputGroups() {
		return inputGroups;
	}

	public ChartProperties setInputGroups(List<SearchCategoryInputGroup> inputGroups) {
		this.inputGroups = inputGroups;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public ChartProperties setTitle(String title) {
		this.title = title;
		return this;
	}
*/
	/*
	public boolean isReady() {
		for (SearchCategoryGroup g : getGroups()) {
			for (SearchCategory c : g.getCategories()) {
				if (c.isReady()) {
					return true;
				}
			}
		}
		return false;
	}
	*/
}
