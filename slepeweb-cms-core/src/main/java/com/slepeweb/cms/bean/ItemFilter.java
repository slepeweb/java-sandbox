package com.slepeweb.cms.bean;

public class ItemFilter {

	private String[] types, linkNames;

	public String[] getTypes() {
		return types;
	}

	public ItemFilter setTypes(String[] types) {
		this.types = types;
		return this;
	}

	public String[] getLinkNames() {
		return linkNames;
	}

	public ItemFilter setLinkNames(String[] linkNames) {
		this.linkNames = linkNames;
		return this;
	}
	
}
