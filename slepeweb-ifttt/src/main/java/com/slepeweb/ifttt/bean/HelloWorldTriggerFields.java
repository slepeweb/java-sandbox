package com.slepeweb.ifttt.bean;

import org.apache.commons.lang3.StringUtils;

public class HelloWorldTriggerFields extends RequestBodyFields {

	private String greeting = "";
	
	@Override
	public boolean isBlank() {
		return StringUtils.isBlank(getGreeting());
	}
	
	public String getGreeting() {
		return greeting;
	}

	public HelloWorldTriggerFields setGreeting(String s) {
		this.greeting = s;
		return this;
	}

}
