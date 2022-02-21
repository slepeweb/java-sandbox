package com.slepeweb.cms.bean;

import com.slepeweb.cms.bean.guidance.IValidator;

public class LinkNameOption {
	private String name;
	private IValidator validator;
	
	public LinkNameOption(String a, IValidator b) {
		this.name = a;
		this.validator = b;
	}

	public String getName() {
		return name;
	}

	public IValidator getValidator() {
		return validator;
	}
}
