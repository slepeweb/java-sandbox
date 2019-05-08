package com.slepeweb.money.bean.chart;

import java.util.ArrayList;
import java.util.List;

public class ChartProperties {

	private int fromYear = 2000, numYears = 20;
	private List<ChartCategoryGroup> groups = new ArrayList<ChartCategoryGroup>();
	
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

	public List<ChartCategoryGroup> getGroups() {
		return this.groups;
	}

	public ChartProperties setGroups(List<ChartCategoryGroup> groups) {
		this.groups = groups;
		return this;
	}
	
	public boolean isReady() {
		for (ChartCategoryGroup g : getGroups()) {
			for (ChartCategory c : g.getCategories()) {
				if (c.isReady()) {
					return true;
				}
			}
		}
		return false;
	}
}
