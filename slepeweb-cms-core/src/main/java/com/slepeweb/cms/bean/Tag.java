package com.slepeweb.cms.bean;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class Tag extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String value;
	private Item item;
	
	public void assimilate(Object obj) {
		if (obj instanceof Tag) {
			Tag t = (Tag) obj;
			setItem(t.getItem());
			setValue(t.getValue());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			getItem() != null &&
			getItem().getId() != null &&
			StringUtils.isNotBlank(getValue());
	}
	
	public Long getId() {
		return NO_ID;
	}
	
	@Override
	public String toString() {
		return String.format("Tags for itemId %d: '%s'", getItem().getId(), getValue());
	}

	@Override
	protected CmsBean save() {
		getTagService().save(getItem(), Arrays.asList(getValue()));
		return getTagService().getTag(getItem().getId(), getValue());
	}

	@Override
	protected void delete() {
		// TODO Auto-generated method stub
		
	}

	public Item getItem() {
		return item;
	}

	public Tag setItem(Item item) {
		this.item = item;
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
