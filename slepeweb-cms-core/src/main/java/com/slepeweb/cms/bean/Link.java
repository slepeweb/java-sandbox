package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.except.ResourceException;

public class Link extends CmsBean {
	private static final long serialVersionUID = 1L;

	private Long parentId;
	private Item child;
	private String name = "na", type, data;
	private Integer ordering;
	
	public void assimilate(Object obj) {
		if (obj instanceof Link) {
			Link l = (Link) obj;
			setName(l.getName());
			setType(l.getType());
			setOrdering(l.getOrdering());
			setData(l.getData());
		}
	}
	
	public boolean isDefined4Insert() {
		return
			getParentId() != null &&
			getChild() != null && 
			getChild().getId() != null &&
			StringUtils.isNotBlank(getType()) &&
			StringUtils.isNotBlank(getName());
	}
	
	public Long getId() {
		return NO_ID;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s): %d -> %s", getType(), getName(), getParentId(), getChild().getName());
	}
	
	public Link save() throws ResourceException {
		return getLinkService().save(this);
	}

	public void delete() {
		getLinkService().deleteLink(getParentId(), getChild().getId());
	}
	
	public Item getChild() {
		return child;
	}

	public Link setChild(Item child) {
		this.child = child;
		return this;
	}

	public String getName() {
		return name;
	}

	public Link setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getOrdering() {
		return ordering;
	}

	public Link setOrdering(Integer ordering) {
		this.ordering = ordering;
		return this;
	}

	public String getType() {
		return type;
	}

	public Link setType(String type) {
		this.type = type;
		return this;
	}

	public Long getParentId() {
		return parentId;
	}

	public Link setParentId(Long parentId) {
		this.parentId = parentId;
		return this;
	}

	public String getData() {
		return data;
	}

	public Link setData(String data) {
		this.data = data;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((child == null) ? 0 : child.getId().hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ordering == null) ? 0 : ordering.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		
		if (! equalsIds(obj))
			return false;
		
		Link other = (Link) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ordering == null) {
			if (other.ordering != null)
				return false;
		} else if (!ordering.equals(other.ordering))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	public boolean equalsIds(Object obj) {
		Link other = (Link) obj;
		if (child == null) {
			if (other.child != null)
				return false;
		} else if (!child.getId().equals(other.child.getId()))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;

		return true;
	}
}
