package com.slepeweb.money.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * This class supports a combination of purposes:
 * a) supports rendition of forms (groups)
 * b) in json form, it can be stored in the db (inputGroups)
 */
@JsonIgnoreProperties({})
public class ChartProperties {

	private String title = "";
	private int fromYear = 2015, toYear = 2019;
	private Category_GroupSet categories;
	
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

	public String getTitle() {
		return title;
	}

	public ChartProperties setTitle(String title) {
		this.title = title;
		return this;
	}

	public Category_GroupSet getCategories() {
		return categories;
	}

	public ChartProperties setCategories(Category_GroupSet categories) {
		this.categories = categories;
		return this;
	}
}
