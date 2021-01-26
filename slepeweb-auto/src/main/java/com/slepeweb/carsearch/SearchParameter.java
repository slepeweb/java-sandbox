package com.slepeweb.carsearch;

public class SearchParameter {

	private String name, value;

	public String getName() {
		return name;
	}

	public SearchParameter setName(String name) {
		this.name = name;
		return this;
	}

	public String getValue() {
		return value;
	}

	public SearchParameter setValue(String value) {
		this.value = value;
		return this;
	}
}
