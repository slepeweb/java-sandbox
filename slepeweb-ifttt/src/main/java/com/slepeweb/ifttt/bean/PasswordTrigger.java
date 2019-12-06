package com.slepeweb.ifttt.bean;

import org.codehaus.jackson.annotate.JsonSetter;

public class PasswordTrigger extends Request {

	private PasswordTriggerFields fields;
	
	@Override
	public boolean isMissingFields() {
		return this.fields == null || this.fields.isBlank();
	}
	
	public PasswordTriggerFields getFields() {
		return fields;
	}
	
	@JsonSetter("triggerFields") 
	public PasswordTrigger setFields(PasswordTriggerFields fields) {
		this.fields = fields;
		return this;
	}
	
}
