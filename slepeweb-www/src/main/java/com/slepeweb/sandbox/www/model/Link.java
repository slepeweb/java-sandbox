package com.slepeweb.sandbox.www.model;

import java.util.List;

public class Link {
	private String label, href;
	private Image thumbnail;
	private boolean trusted, selected;
	private List<Link> children;
	
	public String getLabel() {
		return label;
	}
	
	public Link setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public String getHref() {
		return href;
	}
	
	public Link setHref(String href) {
		this.href = href;
		return this;
	}
	
	public Image getThumbnail() {
		return thumbnail;
	}
	
	public Link setThumbnail(Image thumbnail) {
		this.thumbnail = thumbnail;
		return this;
	}
	
	public boolean isTrusted() {
		return trusted;
	}
	
	public Link setTrusted(boolean trusted) {
		this.trusted = trusted;
		return this;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public Link setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	public List<Link> getChildren() {
		return children;
	}
	
	public void setChildren(List<Link> children) {
		this.children = children;
	}
}
