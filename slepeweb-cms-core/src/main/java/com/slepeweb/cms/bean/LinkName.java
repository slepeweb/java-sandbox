package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

public class LinkName extends CmsBean {
	private static final long serialVersionUID = 1L;
	public static final String std = "std";
	public static final String MAIN = "main";
	public static final String RIGHT_SIDE = "rightside";
	public static final String LEFT_SIDE = "leftside";

	private Long id;
	private Long siteId;
	private Long linkTypeId;
	private String name, validatorClass;
	
	public void assimilate(Object obj) {
		if (obj instanceof LinkName) {
			LinkName ln = (LinkName) obj;
			setSiteId(ln.getSiteId());
			setLinkTypeId(ln.getLinkTypeId());
			setName(ln.getName()).
			setValidatorClass(ln.getValidatorClass());
		}
	}
	
	public boolean isDefined4Insert() {
		return
			getSiteId() != null &&
			getLinkTypeId() != null &&
			StringUtils.isNotBlank(getName());
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public LinkName save() {
		return getLinkNameService().save(this);
	}

	public void delete() {
		getLinkNameService().deleteLinkName(this);
	}
	
	public String getName() {
		return name;
	}

	public LinkName setName(String name) {
		this.name = name;
		return this;
	}

	public Long getSiteId() {
		return siteId;
	}

	public LinkName setSiteId(Long siteId) {
		this.siteId = siteId;
		return this;
	}

	public Long getLinkTypeId() {
		return linkTypeId;
	}

	public LinkName setLinkTypeId(Long id) {
		this.linkTypeId = id;
		return this;
	}

	public String getValidatorClass() {
		return validatorClass;
	}

	public LinkName setValidatorClass(String c) {
		this.validatorClass = c;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((linkTypeId == null) ? 0 : linkTypeId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
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
		LinkName other = (LinkName) obj;
		if (linkTypeId == null) {
			if (other.linkTypeId != null)
				return false;
		} else if (!linkTypeId.equals(other.linkTypeId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (siteId == null) {
			if (other.siteId != null)
				return false;
		} else if (!siteId.equals(other.siteId))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

	public LinkName setId(Long id) {
		this.id = id;
		return this;
	}

}
