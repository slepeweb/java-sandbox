package com.slepeweb.cms.bean;

/*
 * This been has been invented to work around method signature confusion in Item class.
 */
public class StringWrapper {
	
	private String value;
	
	public StringWrapper(String s) {
		this.value = s;
	}

	public String getValue() {
		return this.value;
	}
}
