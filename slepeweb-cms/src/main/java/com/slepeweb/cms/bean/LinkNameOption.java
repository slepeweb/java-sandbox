package com.slepeweb.cms.bean;

public class LinkNameOption {
	private String name, validator;
	
	public LinkNameOption(String a, String b) {
		this.name = a;
		this.validator = b;
	}

	public String getName() {
		return name;
	}

	public String getValidator() {
		return validator;
	}
}
