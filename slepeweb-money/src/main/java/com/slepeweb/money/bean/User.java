package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	public static String USER_ATTR = "_user";
	public static String BROWSER_ROLE = "browser";
	public static String EDITOR_ROLE = "editor";
	public static String ADMIN_ROLE = "admin";
	
	private Long id;
	private String name, alias, password, roles;
	private boolean enabled;
	private List<String> roleList = new ArrayList<String>();
	
	@Override
	public String toString() {
		return String.format("%s [%s]", this.name, this.alias);
	}
	
	public boolean isDefined4Insert() {
		return false;
	}
	
	public boolean isInDatabase() {
		return this.id > 0;
	}
	
	public void assimilate(User u) {
		setName(u.getName()).
		setAlias(u.getAlias()).
		setPassword(u.getPassword()).
		setEnabled(u.isEnabled()).
		setRoles(u.getRoles());
	}
	public User setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public User setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public User setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public String getRoles() {
		return roles;
	}

	public User setRoles(String roles) {
		this.roles = roles;
		for (String r : roles.split(",")) {
			addRole(r.trim());
		}
		return this;
	}

	public List<String> getRoleList() {
		return this.roleList;
	}

	public boolean isBrowser() {
		return hasRole(BROWSER_ROLE);
	}
	
	public boolean isEditor() {
		return hasRole(EDITOR_ROLE);
	}
	
	public boolean isAdmin() {
		return hasRole(ADMIN_ROLE);
	}
	
	public boolean hasRole(String role) {
		return this.roleList != null && this.roleList.contains(role);
	}

	public boolean hasRole(String[] roles) {
		if (this.roleList != null) {
			for (String role : roles) {
				if (this.roleList.contains(role)) {
					return true;
				}
			}
		}
		return false;
	}

	public User addRole(String r) {
		this.roleList.add(r);
		return this;
	}
	
	public User setRoleList(List<String> roles) {
		this.roleList = roles;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		return true;
	}

}
