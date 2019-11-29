package com.slepeweb.ifttt.bean;

import org.codehaus.jackson.annotate.JsonSetter;

public class HelloWorldTrigger extends Request {

	private HelloWorldTriggerFields fields;
	
	@Override
	public boolean isMissingFields() {
		return this.fields == null || this.fields.isBlank();
	}
	
	public HelloWorldTriggerFields getFields() {
		return fields;
	}
	
	@JsonSetter("triggerFields") 
	public HelloWorldTrigger setFields(HelloWorldTriggerFields fields) {
		this.fields = fields;
		return this;
	}
	
}
