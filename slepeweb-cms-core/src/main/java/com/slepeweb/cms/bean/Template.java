package com.slepeweb.cms.bean;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Template extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String name, controller;
	private Long id, siteId, itemTypeId;
	private ItemType itemType;
	private boolean admin;
	
	public void assimilate(Object obj) {
		if (obj instanceof Template) {
			Template t = (Template) obj;
			setName(t.getName());
			setController(t.getController());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getController());
	}
	
	public Template save() {
		return getCmsService().getTemplateService().save(this);
	}
	
	public void delete() {
		// TODO: Implement
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", getName(), getController());
	}

	public String getName() {
		return name;
	}

	public Template setName(String name) {
		this.name = name;
		return this;
	}

	public String getController() {
		return controller;
	}

	public Template setController(String forward) {
		this.controller = forward;
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
	
	public boolean isAdmin() {
		return admin;
	}

	public Template setAdmin(boolean admin) {
		this.admin = admin;
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
		result = prime * result + (admin ? 1231 : 1237);
		result = prime * result + ((controller == null) ? 0 : controller.hashCode());
		result = prime * result + ((itemTypeId == null) ? 0 : itemTypeId.hashCode());
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
		if (admin != other.admin)
			return false;
		if (controller == null) {
			if (other.controller != null)
				return false;
		} else if (!controller.equals(other.controller))
			return false;
		if (itemTypeId == null) {
			if (other.itemTypeId != null)
				return false;
		} else if (!itemTypeId.equals(other.itemTypeId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
