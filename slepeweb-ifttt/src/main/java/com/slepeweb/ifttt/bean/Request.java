package com.slepeweb.ifttt.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonSetter;

public abstract class Request {

	// Uniquely identifies my service
	public static final String SERVICE_KEY = "O4zPFIy-20lmh5ALYkEpxe7jhZBRu8mTwgy7oGOxE3KHzgje85_ZdigvGtXg0hXz";
	
	// Required by both Triggers and Actions
	private Source source;
	private User user;
	
	// Only appropriate for Triggers
	private String identity;
	private int limit = 50;
	
	// Invented by me to support this chain of Applets
	private Date created;
	
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

	public Date getCreated() {
		return created;
	}

	public Request setCreated(Date created) {
		this.created = created;
		return this;
	}
}
