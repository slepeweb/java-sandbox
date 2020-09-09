package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.except.ResourceException;

public class Role extends CmsBean {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	
	public void assimilate(Object obj) {
		if (obj instanceof Role) {
			Role r = (Role) obj;
			setName(r.getName());
		}
	}
	
	public boolean isDefined4Insert() {
		return StringUtils.isNotBlank(getName());
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
		return getName();
	}
	
	public Role setId(Long id) {
		this.id = id;
		return this;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Role setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Role other = (Role) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
