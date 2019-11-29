package com.slepeweb.ifttt.bean;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonSetter;

public abstract class Request {

	public static final String SERVICE_KEY = "vQAqaeOMiU0p9vO-mlNv9SESl7KacisWVTV3Wf7iduKhbvZrVdCrAgH0_oEJX1XE";
	
	private String identity = "92429d82a41e93048";
	private Source source;
	private User user;
	private int limit = 50;
	
	public abstract boolean isMissingFields();
	
	public String getIdentity() {
		return identity;
	}
	
	@JsonSetter("trigger_identity") 
	public Request setIdentity(String identity) {
		this.identity = identity;
		return this;
	}
	
	public Source getSource() {
		return source;
	}
	
	@JsonSetter("ifttt_source") 
	public Request setSource(Source source) {
		this.source = source;
		return this;
	}
	
	public User getUser() {
		return user;
	}
	
	@JsonSetter("user") 
	public Request setUser(User user) {
		this.user = user;
		return this;
	}
	
	@JsonAnySetter
	public void setDummy(String name, Object value) {
	
	}

	public int getLimit() {
		return limit;
	}

	@JsonSetter("limit") 
	public Request setLimit(int limit) {
		this.limit = limit;
		return this;
	}
}
