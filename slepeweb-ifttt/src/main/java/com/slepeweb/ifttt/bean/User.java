package com.slepeweb.ifttt.bean;

public class User {
	private String timezone = "America/Los_Angeles";

	public String getTimezone() {
		return timezone;
	}

	public User setTimezone(String timezone) {
		this.timezone = timezone;
		return this;
	}
}
