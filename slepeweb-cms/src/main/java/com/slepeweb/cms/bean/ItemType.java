package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

public class ItemType {
	private Long id;
	private String name;
	
	public void assimilate(ItemType it) {
		setName(it.getName());
	}
	
	public boolean isDefined() {
		return 
			StringUtils.isNotBlank(getName());
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
