package com.slepeweb.sandbox.orm;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.password.BasicPasswordEncryptor;

public class User {
	
    private Integer id;
	private String name, alias, password, encryptedPassword;
	private String[] selectedRoles;
	private Short demoUser = 0;

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

	public void setName(String name) {
		this.name = name;
	}

	@Size(min=4, max=32, message="Please enter your login name (min. 4, max. 32 chars)")
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
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

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public void setDemoUser(Short demoUser) {
		this.demoUser = demoUser;
	}

}
