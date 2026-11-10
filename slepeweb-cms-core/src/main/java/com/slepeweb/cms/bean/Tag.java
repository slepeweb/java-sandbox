package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class Tag extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String value;
	private Long itemId, siteId;
	
	public Tag() {}
	
	public Tag(Item i, String value) {
		this.itemId = i.getId();
		this.siteId = i.getSite().getId();
		this.value = value;
	}
	
	public void assimilate(Object obj) {
		if (obj instanceof Tag) {
			Tag t = (Tag) obj;
			setItemId(t.getItemId());
			setSiteId(t.getSiteId());
			setValue(t.getValue());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			getSiteId() != null &&
			getItemId() != null &&
			StringUtils.isNotBlank(getValue());
	}
	
	public Long getId() {
		return NO_ID;
	}
	
	@Override
	public String toString() {
		return getValue();
	}

	@Deprecated
	// NEVER USED
	protected CmsBean save() {
		return null;
	}

	@Override
	protected void delete() {
		// TODO Auto-generated method stub
		
	}
	
	public static List<String> toValues(List<Tag> tags) {
		List<String> result = new ArrayList<String>(tags.size());
		
		for (Tag t : tags) {
			result.add(t.getValue());
		}
		return result;
	}

	public Long getSiteId() {
		return siteId;
	}

	public Tag setSiteId(Long l) {
		this.siteId = l;
		return this;
	}

	public Long getItemId() {
		return itemId;
	}

	public Tag setItemId(Long l) {
		this.itemId = l;
		return this;
	}

	public String getValue() {
		return value;
	}

	public Tag setValue(String value) {
		this.value = value;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(itemId, siteId, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		return Objects.equals(itemId, other.itemId) && Objects.equals(siteId, other.siteId)
				&& Objects.equals(value, other.value);
	}

}
