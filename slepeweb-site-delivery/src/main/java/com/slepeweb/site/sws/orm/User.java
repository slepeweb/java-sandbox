package com.slepeweb.site.sws.orm;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.password.BasicPasswordEncryptor;

public class User {
	
    private Integer id;
    private Long userFormPageId;
	private String name, alias, password, encryptedPassword;
	private String[] selectedRoles;
	private Short demoUser = 1, enabled = 1;

    private Set<Role> roles = new HashSet<Role>();
	
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	for (Role r : getRoles()) {
    		if (sb.length() > 0) {
    			sb.append(", ");
    		}
    		sb.append(r.getName());
    	}
    	
    	return String.format("%s (%s)", getName(), sb.toString());
    }
    
	public Integer getUserId() {
		return getId();
	}
	
	public User setUserId(Integer userId) {
		setId(userId);
		return this;
	}
	
	public String getUserName() {
		return getName();
	}
	
	public User setUserName(String userName) {
		setName(userName);
		return this;
	}
	
	public boolean hasRole(String name) {
		if (getRoles() != null) {
			for (Role r : getRoles()) {
				if (r.getName().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void encryptPasswordIfNotBlank() {
		if (! StringUtils.isBlank(getPassword())) {
			BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
			setEncryptedPassword(passwordEncryptor.encryptPassword(getPassword()));
		}
	}
	
	public void assimilate(User u) {
		setName(u.getName());
		setAlias(u.getAlias());
		setPassword(u.getPassword());
		setEncryptedPassword(u.getEncryptedPassword());
		setRoles(u.getRoles());
	}
	
	@Size(min=2, max=32, message="Please enter your full name (32 chars max.)")
    public String getName() {
		return name;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	@Size(min=4, max=32, message="Please enter your login name (min. 4, max. 32 chars)")
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

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public User setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public User setId(Integer id) {
		this.id = id;
		return this;
	}

	@Valid
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String[] getSelectedRoles() {
		return selectedRoles;
	}

	public void setSelectedRoles(String[] selectedRoles) {
		this.selectedRoles = selectedRoles;
	}

	public Short getDemoUser() {
		return demoUser;
	}

	public User setDemoUser(Short demoUser) {
		this.demoUser = demoUser;
		return this;
	}

	public Long getUserFormPageId() {
		return userFormPageId;
	}

	public User setUserFormPageId(Long userFormPageId) {
		this.userFormPageId = userFormPageId;
		return this;
	}

	public Short getEnabled() {
		return enabled;
	}

	public User setEnabled(Short enabled) {
		this.enabled = enabled;
		return this;
	}

}
