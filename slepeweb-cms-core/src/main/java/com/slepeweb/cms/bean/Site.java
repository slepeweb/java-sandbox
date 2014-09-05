package com.slepeweb.cms.bean;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Site extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String name, hostname;
	private Long id;
		
	public void assimilate(Object obj) {
		if (obj instanceof Site) {
			Site s = (Site) obj;
			setName(s.getName());
			setHostname(s.getHostname());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getHostname());
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", getName(), getHostname());
	}
	
	public Site save() {
		return getSiteService().save(this);
	}
	
	public void delete() {
		getSiteService().deleteSite(getId());
	}
	
	public Item getItem(String path) {
		return getItemService().getItem(getId(), path);
	}
	
	public List<Template> getAvailableTemplates() {
		return getCmsService().getTemplateService().getAvailableTemplates(getId());
	}
	
	public List<ItemType> getAvailableItemTypes() {
		return getCmsService().getItemTypeService().getAvailableItemTypes();
	}
	
	public String getName() {
		return name;
	}
	
	public Site setName(String name) {
		this.name = name;
		return this;
	}
	
	public Long getId() {
		return id;
	}
	
	public Site setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getHostname() {
		return hostname;
	}

	public Site setHostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
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
		Site other = (Site) obj;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
