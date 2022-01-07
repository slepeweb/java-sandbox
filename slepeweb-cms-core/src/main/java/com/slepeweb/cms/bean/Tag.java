package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Tag extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String value;
	private Long itemId, siteId;
	
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
		return String.format("Tags for itemId %d: '%s'", getItemId(), getValue());
	}

	@Override
	// Saves a list of tags, and returns one, in order to satisfy the CmsBean interface
	protected CmsBean save() {
		getTagService().save(getSiteId(), getItemId(), Arrays.asList(getValue()));
		return getTagService().getTag4ItemWithValue(getItemId(), getValue());
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

}
