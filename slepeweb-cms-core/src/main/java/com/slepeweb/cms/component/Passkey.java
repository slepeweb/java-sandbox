package com.slepeweb.cms.component;

<<<<<<< Upstream, based on branch 'master' of https://github.com/slepeweb/java-sandbox.git
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
=======
import org.apache.commons.lang3.StringUtils;

public class Passkey {

	private String id, alias, key;
	
	public Passkey() {}
	
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
>>>>>>> 5c146fe cms-d: pdf gen, stage 1
	}
}
