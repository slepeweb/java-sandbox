package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

public class ChartProperties {

	private int fromYear = 2000, numYears = 20;
	private List<CategoryGroup> groups = new ArrayList<CategoryGroup>();
	
	public int getFromYear() {
		return fromYear;
	}
	
	public ChartProperties setFromYear(int fromYear) {
		this.fromYear = fromYear;
		return this;
	}
	
	public int getNumYears() {
		return numYears;
	}
	
	public ChartProperties setNumYears(int numYears) {
		this.numYears = numYears;
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