package com.slepeweb.cms.bean;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.service.SiteService;

public class Site implements Serializable {
	private static final long serialVersionUID = 1L;
	private Item root;
	private String name, hostname;
	private Long id;
		
	public void assimilate(Site s) {
		setName(s.getName());
		setHostname(s.getHostname());
	}
	
	public boolean isDefined() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getHostname());
	}
	
	public Item getRoot() {
		return root;
	}
	
	public void setRoot(Item root) {
		this.root = root;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}
