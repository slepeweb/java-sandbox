package com.slepeweb.cms.bean;

import java.io.Serializable;

public class Link extends CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long parentId;
	private Item child;
	private LinkType type;
	private String name;
	private Integer ordering;
	
	public enum LinkType {
		binding, relation, inline, shortcut;
	}
	
	public void assimilate(Link l) {
		setName(l.getName());
		setType(l.getType());
		setOrdering(l.getOrdering());
	}
	
	public boolean isDefined4Insert() {
		return
			getParentId() != null &&
			getChild() != null & getChild().getId() != null &&
			getType() != null;
	}
	
	@Override
	public String toString() {
		return String.format("%d -> %d", getParentId(), getChild().getId());
	}
	
	public Link save() {
		return getLinkService().save(this);
	}

	public void delete() {
		getLinkService().deleteLinks(getParentId(), getChild().getId());
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

	public LinkType getType() {
		return type;
	}

	public Link setType(LinkType type) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((child == null) ? 0 : child.getId().hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ordering == null) ? 0 : ordering.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Link other = (Link) obj;
		if (child == null) {
			if (other.child != null)
				return false;
		} else if (!child.getId().equals(other.child.getId()))
			return false;
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
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}