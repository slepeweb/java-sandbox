package com.slepeweb.ifttt.bean;

import org.apache.commons.lang3.StringUtils;

public class PasswordActionFields extends RequestBodyFields {

	private String party = "";

	@Override
	public boolean isBlank() {
		return StringUtils.isBlank(getParty());
	}
	
	public String getParty() {
		return party;
	}

	public PasswordActionFields setParty(String party) {
		this.party = party;
		return this;
	}
}
