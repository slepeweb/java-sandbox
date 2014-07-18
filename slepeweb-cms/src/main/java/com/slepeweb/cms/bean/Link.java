package com.slepeweb.cms.bean;

public class Link {
	private Item parent, child;
	private String name;
	private Integer ordering;
	
	public enum LinkType {
		binding, relation, shortcut;
	}
}
