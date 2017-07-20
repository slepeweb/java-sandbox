package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

public class Template extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String name, forward;
	private Long id, siteId, itemTypeId;
	private ItemType itemType;
	
	public void assimilate(Object obj) {
		if (obj instanceof Template) {
			Template t = (Template) obj;
			setName(t.getName());
			setForward(t.getForward());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getForward());
	}
	
	public Template save() {
		return getCmsService().getTemplateService().save(this);
	}
	
	public void delete() {
		// TODO: Implement
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", getName(), getForward());
	}

	public String getName() {
		return name;
	}

	public Template setName(String name) {
		this.name = name;
		return this;
	}

	public String getForward() {
		return forward;
	}

	public Template setForward(String forward) {
		this.forward = forward;
		return this;
	}

	public Long getId() {
		return id;
	}

	public Template setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getSiteId() {
		return siteId;
	}

	public Template setSiteId(Long siteId) {
		this.siteId = siteId;
		return this;
	}

	public Long getItemTypeId() {
		return itemTypeId;
	}

	public Template setItemTypeId(Long itemTypeId) {
		this.itemTypeId = itemTypeId;
		return this;
	}
	
	public ItemType getItemType() {
		if (this.itemType == null) {
			this.itemType = getItemTypeService().getItemType(getItemTypeId());
		}
		return this.itemType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((forward == null) ? 0 : forward.hashCode());
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
		Template other = (Template) obj;
		if (forward == null) {
			if (other.forward != null)
				return false;
		} else if (!forward.equals(other.forward))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
