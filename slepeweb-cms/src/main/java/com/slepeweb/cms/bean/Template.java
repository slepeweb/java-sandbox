package com.slepeweb.cms.bean;

import java.io.Serializable;

public class Template extends CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name, jspPath;
	private ItemType itemType;
	private Integer id;
	
	public Template save() {
		return null;
	}
	
	public void delete() {
		
	}
	
}
