package com.slepeweb.funds.bean;

import org.apache.commons.lang3.StringUtils;

public class Fund {
	private String name, alias;
	private long id, units;
	
	public void assimilate(Object obj) {
		if (obj instanceof Fund) {
			Fund f = (Fund) obj;
			f.setName(f.getName()).
				setAlias(f.getAlias()).
				setUnits(f.getUnits());
		}
	}
	
	public boolean isDefined4Insert() {
		return  
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getAlias()) &&
			getUnits() > 0L;
	}
	
	@Override
	public String toString() {
		return getAlias();
	}
	
	public long getId() {
		return id;
	}
	
	public Fund setId(long id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public Fund setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public Fund setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	
	public long getUnits() {
		return units;
	}
	
	public Fund setUnits(long units) {
		this.units = units;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
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
		Fund other = (Fund) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		return true;
	}
}
