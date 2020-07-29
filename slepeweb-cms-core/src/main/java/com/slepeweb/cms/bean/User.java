package com.slepeweb.cms.bean;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.except.ResourceException;

public class User extends CmsBean {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name, alias, password;
	private boolean enabled;
	private List<String> roles;
	
	public void assimilate(Object obj) {
		if (obj instanceof User) {
			User u = (User) obj;
			setAlias(u.getAlias());
			setPassword(u.getPassword());
			setEnabled(u.isEnabled());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getAlias()) &&
			StringUtils.isNotBlank(getPassword());
	}
	
	@Override
	protected CmsBean save() throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void delete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return getAlias();
	}
	
	public User setId(Long id) {
		this.id = id;
		return this;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getAlias() {
		return alias;
	}

	public User setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public User setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public List<String> getRoles() {
		if (this.roles == null) {
			this.roles = getCmsService().getUserService().getRoles(getId());
		}
		return this.roles;
	}

	public User setRoles(List<String> roles) {
		this.roles = roles;
		return this;
	}

	public String getName() {
		return name;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
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
		if (enabled != other.enabled)
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
		return true;
	}

}
