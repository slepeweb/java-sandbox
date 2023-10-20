package com.slepeweb.cms.bean;

public class StickyAddNewControls {
	private long lastType, lastTemplate;
	private String lastPosition;
	
	public StickyAddNewControls() {
		this.lastPosition = "below";
	}
	
	public StickyAddNewControls(String s, long te, long ty) {
		this.lastPosition = s;
		this.lastTemplate = te;
		this.lastType = ty;
	}

	public StickyAddNewControls(String s) {
		this();
		
		if (s != null) {
			String[] parts = s.split(",");
			if (parts.length > 0) {
				this.lastPosition = parts[0].trim();			
				this.lastTemplate = parts.length > 1 ? Long.valueOf(parts[1].trim()) : 0;
				this.lastType = parts.length > 2 ? Long.valueOf(parts[2].trim()) : 0;
			}
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s, %d, %d", this.lastPosition, this.lastTemplate, this.lastType);
	}
	
	public long getLastType() {
		return lastType;
	}
	
	public StickyAddNewControls setLastType(long lastType) {
		this.lastType = lastType;
		return this;
	}
	
	public long getLastTemplate() {
		return lastTemplate;
	}
	
	public StickyAddNewControls setLastTemplate(long lastTemplate) {
		this.lastTemplate = lastTemplate;
		return this;
	}
	
	public String getLastPosition() {
		return lastPosition;
	}
	
	public StickyAddNewControls setLastPosition(String lastPosition) {
		this.lastPosition = lastPosition;
		return this;
	}
}
