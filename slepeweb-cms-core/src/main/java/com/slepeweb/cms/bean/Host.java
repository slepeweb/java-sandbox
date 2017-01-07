package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

public class Host extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String name;
	private Long id, siteId;
		
	public void assimilate(Object obj) {
		if (obj instanceof Host) {
			Host h = (Host) obj;
			setName(h.getName());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			getSiteId() > 0;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public Host save() {
		return getHostService().save(this);
	}
	
	public void delete() {
		getHostService().deleteHost(this);
	}
	
	public String getName() {
		return name;
	}
	
	public Host setName(String name) {
		this.name = name;
		return this;
	}
	
	public Long getId() {
		return id;
	}
	
	public Host setId(Long id) {
		this.id = id;
		return this;
	}
	
	public Long getSiteId() {
		return siteId;
	}

	public Host setSiteId(Long siteId) {
		this.siteId = siteId;
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
		Host other = (Host) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
