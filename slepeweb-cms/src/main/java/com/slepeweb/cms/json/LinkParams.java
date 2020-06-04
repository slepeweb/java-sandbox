package com.slepeweb.cms.json;

public class LinkParams {
	private long childId;
	private int linkId, state;
	private String type, name, data;
	
	public long getChildId() {
		return childId;
	}
	
	public void setChildId(long childId) {
		this.childId = childId;
	}
	
	public int getLinkId() {
		return linkId;
	}
	
	public void setLinkId(int id) {
		this.linkId = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
