package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

public class LinkType extends CmsBean {
	private static final long serialVersionUID = 1L;
	public static final String binding = "binding";
	public static final String relation = "relation";
	public static final String inline = "inline";
	public static final String component = "component";
	public static final String shortcut = "shortcut";

	private Long id;
	private String name;
	
	public void assimilate(Object obj) {
		if (obj instanceof LinkType) {
			LinkType lt = (LinkType) obj;
			setName(lt.getName());
		}
	}
	
	public boolean isDefined4Insert() {
		return StringUtils.isNotBlank(getName());
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public LinkType save() {
		return getLinkTypeService().save(this);
	}

	public void delete() {
		getLinkTypeService().deleteLinkType(this);
	}

	public Long getId() {
		return id;
	}

	public LinkType setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public LinkType setName(String name) {
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
		LinkType other = (LinkType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
