package com.slepeweb.money.bean.chart;

import java.util.ArrayList;
import java.util.List;

public class ChartCategoryGroup {
	
	public static final int MAX = 5;

	private String label = "";
	private List<ChartCategory> categories = new ArrayList<ChartCategory>();
	
	public String getLabel() {
		return label;
	}
	
	public ChartCategoryGroup setLabel(String label) {
		this.label = label;
		return this;
	}
	
	// Always returns at least 3 options
	public List<ChartCategory> getCategories() {
		if (this.categories.size() < ChartCategory.MAX) {
			for (int i = this.categories.size(); i < ChartCategory.MAX; i++) {
				this.categories.add(new ChartCategory());
			}
		}
		return categories;
	}
	
	public ChartCategoryGroup setCategories(List<ChartCategory> cats) {
		this.categories = cats;
		return this;
	}
	
}
