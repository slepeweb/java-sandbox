package com.slepeweb.money.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"defined4Insert", "inDatabase", "legacy", "expense"})
public class SearchCategory extends Category {
	
	private boolean exclude;
	
	public void assimilate(Object obj) {
		if (obj instanceof SearchCategory) {
			SearchCategory c = (SearchCategory) obj;
			setMajor(c.getMajor());
			setMinor(c.getMinor());
			setOrigId(c.getOrigId());
			setExpense(c.isExpense());
		}
	}
	
	public boolean isExclude() {
		return exclude;
	}

	public Category setExclude(boolean exclude) {
		this.exclude = exclude;
		return this;
	}
}
