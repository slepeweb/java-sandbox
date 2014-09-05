package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

public class SiteConfig extends CmsBean {
	private static final long serialVersionUID = 1L;
	private long siteId;
	private String name, value;
	
	public void assimilate(Object obj) {
		if (obj instanceof SiteConfig) {
			SiteConfig t = (SiteConfig) obj;
			setName(t.getName());
			setValue(t.getValue());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getValue());
	}
	
	public SiteConfig save() {
		return getCmsService().getSiteConfigService().save(this);
	}
	
	@Override
	public String toString() {
		return String.format("%s -> [%s]", getName(), getValue());
	}
	
	public void delete() {
		// TODO: Implement
	}
	public long getSiteId() {
		return siteId;
	}
	
	public SiteConfig setSiteId(long siteId) {
		this.siteId = siteId;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public SiteConfig setName(String key) {
		this.name = key;
		return this;
	}
	
	public String getValue() {
		return value;
	}
	
	public SiteConfig setValue(String value) {
		this.value = value;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (siteId ^ (siteId >>> 32));
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		SiteConfig other = (SiteConfig) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (siteId != other.siteId)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
