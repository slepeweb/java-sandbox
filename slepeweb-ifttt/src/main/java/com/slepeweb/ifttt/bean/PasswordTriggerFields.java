package com.slepeweb.ifttt.bean;

import org.apache.commons.lang3.StringUtils;

public class PasswordTriggerFields extends RequestBodyFields {

	private String party = "", password = "";
	
	@Override
	public boolean isBlank() {
		return StringUtils.isBlank(getParty());
	}
	
	public String getParty() {
		return party;
	}

	public PasswordTriggerFields setParty(String s) {
		this.party = s;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public PasswordTriggerFields setPassword(String s) {
		this.password = s;
		return this;
	}
}
