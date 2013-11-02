package com.slepeweb.sandbox.www.model;

import java.util.List;

public class User {
	private String name, alias;
	private List<Role> roles;
	
	public enum Role {
		PUBLIC, ADMIN, AGENT, FRIEND;
	}

	public String getName() {
		return name;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public User setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}
