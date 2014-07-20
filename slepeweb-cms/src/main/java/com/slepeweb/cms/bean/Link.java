package com.slepeweb.cms.bean;

public class Link {
	private Item parent, child;
	private LinkType type;
	private String name;
	private Integer ordering;
	
	public enum LinkType {
		binding, relation, inline, shortcut;
	}
	
	public boolean isDefined4Insert() {
		return
			getParent() != null && getParent().getId() != null &&
			getChild() != null & getChild().getId() != null &&
			getType() != null;
	}

	public Item getParent() {
		return parent;
	}

	public void setParent(Item parent) {
		this.parent = parent;
	}

	public Item getChild() {
		return child;
	}

	public void setChild(Item child) {
		this.child = child;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrdering() {
		return ordering;
	}

	public void setOrdering(Integer ordering) {
		this.ordering = ordering;
	}

	public LinkType getType() {
		return type;
	}

	public void setType(LinkType type) {
		this.type = type;
	}
}
