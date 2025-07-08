package com.slepeweb.cms.component;

import org.apache.commons.lang3.StringUtils;

public class Passkey {

	private String id, alias, key;
	
	public Passkey(String encoded) {
		String[] parts = encoded.split("\\$");
		if (parts.length == 3) {
			this.id = parts[0];
			this.alias = parts[1];
			this.key = parts[2];
		}
	}
	
	public Passkey(String id, String alias, String key) {
		this.id = id;
		this.alias = alias;
		this.key = key;
	}
	
	public String getId() {
		return id;
	}

	public Passkey setId(String id) {
		this.id = id;
		return this;
	}

	public String getKey() {
		return key;
	}

	public Passkey setKey(String key) {
		this.key = key;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public Passkey setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public String encode() {
		return String.format("%s$%s$%s", this.id, this.alias, this.key);
	}
	
	public boolean isReady() {
		return 
			StringUtils.isNotBlank(this.id) &&
			StringUtils.isNotBlank(this.alias) &&
			StringUtils.isNotBlank(this.key);
	}
}
