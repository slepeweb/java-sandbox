package com.slepeweb.ifttt.bean;

import org.codehaus.jackson.annotate.JsonSetter;

public class PasswordAction extends Request {

	private PasswordActionFields fields;
	
	@Override
	public boolean isMissingFields() {
		return this.fields == null || this.fields.isBlank();
	}
	
	public PasswordActionFields getFields() {
		return fields;
	}
	
	@JsonSetter("actionFields") 
	public PasswordAction setFields(PasswordActionFields fields) {
		this.fields = fields;
		return this;
	}
	
}
