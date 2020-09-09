package com.slepeweb.cms.bean;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.except.ResourceException;

public class User extends CmsBean {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String firstName, lastName, email, phone, password, secret;
	private boolean enabled, loggedIn;
	private List<Role> roles;
	
	public void assimilate(Object obj) {
		if (obj instanceof User) {
			User u = (User) obj;
			setFirstName(u.getFirstName());
			setLastName(u.getLastName());
			setEmail(u.getEmail());
			setPhone(u.getPhone());
			setPassword(u.getPassword());
			setEnabled(u.isEnabled());
			setSecret(u.getSecret());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getFirstName()) &&
			StringUtils.isNotBlank(getLastName()) &&
			StringUtils.isNotBlank(getEmail()) &&
			StringUtils.isNotBlank(getPhone());
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
		return getFullName();
	}
	
	public User setId(Long id) {
		this.id = id;
		return this;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFullName() {
		StringBuilder sb = new StringBuilder();
		if (this.firstName != null) {
			sb.append(this.firstName).append(" ");
		}
		if (this.lastName != null) {
			sb.append(this.lastName);
		}
		
		return sb.toString().trim();
	}

	public User setLastName(String name) {
		this.lastName = name;
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

	public List<Role> getRoles() {
		if (this.roles == null) {
			this.roles = getCmsService().getUserService().getRoles(getId());
		}
		return this.roles;
	}

	public User setRoles(List<Role> roles) {
		this.roles = roles;
		return this;
	}

	public User addRole(Role r) {
		getRoles().add(r);
		return this;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public User setFirstName(String name) {
		this.firstName = name;
		return this;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public User setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
		return this;
	}

	public String getSecret() {
		return secret;
	}

	public User setSecret(String secret) {
		this.secret = secret;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public User setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
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
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}

}
