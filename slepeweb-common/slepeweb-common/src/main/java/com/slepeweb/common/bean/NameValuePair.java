package com.slepeweb.common.bean;

public class NameValuePair {

	private String name, value;
	
	public NameValuePair(String k, String v) {
		this.name = k;
		this.value = v;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}
	
}
