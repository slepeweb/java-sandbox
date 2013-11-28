package com.slepeweb.sandbox.mongo;

import java.util.List;

public class MongoUser {
	private String name, alias, password, encryptedPassword;
	private List<Role> roles;
	
	public enum Role {
		PUBLIC, ADMIN, AGENT, FRIEND;
	}
	
	public boolean hasRole(Role role) {
		if (getRoles() != null) {
			return getRoles().contains(role);
		}
		return false;
	}


	public String getName() {
		return name;
	}

	public MongoUser setName(String name) {
		this.name = name;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public MongoUser setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public MongoUser setRoles(List<Role> roles) {
		this.roles = roles;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public MongoUser setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
		return this;
	}

}
