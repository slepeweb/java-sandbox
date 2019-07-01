package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({"ready"})
public class ChartProperties {

	private int fromYear = 2015, toYear = 2019;
	private List<CategoryGroup> groups = new ArrayList<CategoryGroup>();
	
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

	public List<CategoryGroup> getGroups() {
		return this.groups;
	}

	public ChartProperties setGroups(List<CategoryGroup> groups) {
		this.groups = groups;
		return this;
	}
	
	public boolean isReady() {
		for (CategoryGroup g : getGroups()) {
			for (CategoryInput c : g.getCategories()) {
				if (c.isReady()) {
					return true;
				}
			}
		}
		return false;
	}
}
