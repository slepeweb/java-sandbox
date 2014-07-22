package com.slepeweb.cms.bean;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Site extends CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Item root;
	private String name, hostname;
	private Long id;
		
	public void assimilate(Site s) {
		setName(s.getName());
		setHostname(s.getHostname());
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getHostname());
	}
	
	public Item getItem(String path) {
		if (isServiced()) {
			return getItemService().getItem(getId(), path);
		}
		return null;
	}
	
	public void addItem(Item i) {
		if (isServiced()) {
			i.setSite(this);
			getItemService().insertItem(i);
		}
	}
	
	@Override
	public boolean isServiced() {
		return getItemService() != null;
	}
	
	public Item getRoot() {
		return root;
	}
	
	public Site setRoot(Item root) {
		this.root = root;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public Site setName(String name) {
		this.name = name;
		return this;
	}
	
	public Long getId() {
		return id;
	}
	
	public Site setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getHostname() {
		return hostname;
	}

	public Site setHostname(String hostname) {
		this.hostname = hostname;
		return this;
	}
}
