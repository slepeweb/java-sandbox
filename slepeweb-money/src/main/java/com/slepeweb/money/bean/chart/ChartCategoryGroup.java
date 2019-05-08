package com.slepeweb.money.bean.chart;

import java.util.ArrayList;
import java.util.List;

public class ChartCategoryGroup {
	
	private String label = "";
	private List<ChartCategory> categories = new ArrayList<ChartCategory>();
	
	public String getLabel() {
		return label;
	}
	
	public ChartCategoryGroup setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public List<ChartCategory> getCategories() {
		return this.categories;
	}
	
	public ChartCategoryGroup setCategories(List<ChartCategory> cats) {
		this.categories = cats;
		return this;
	}
	
}
