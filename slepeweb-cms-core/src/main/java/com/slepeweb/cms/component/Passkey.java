package com.slepeweb.cms.component;

public class Passkey {

	private String id, key;
	
	public Passkey() {}
	
	public Passkey(String encoded) {
		String[] parts = encoded.split("\\$");
		if (parts.length == 2) {
			this.id = parts[0];
			this.key = parts[1];
		}
	}
	
	public Passkey(String id, String key) {
		this.id = id;
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

	public String encode() {
		return String.format("%s$%s", this.id, this.key);
	}
}
