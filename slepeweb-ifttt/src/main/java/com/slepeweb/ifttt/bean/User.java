package com.slepeweb.ifttt.bean;

public class User {
	private String timezone = "America/Los_Angeles";
	private String name, id, url;

	public String getTimezone() {
		return timezone;
	}

	public User setTimezone(String timezone) {
		this.timezone = timezone;
		return this;
	}

	public String getName() {
		return name;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	public String getId() {
		return id;
	}

	public User setId(String id) {
		this.id = id;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public User setUrl(String url) {
		this.url = url;
		return this;
	}
}
