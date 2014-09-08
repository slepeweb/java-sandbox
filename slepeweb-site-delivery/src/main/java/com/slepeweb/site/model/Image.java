package com.slepeweb.site.model;

import java.io.Serializable;

public class Image implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title, src, alt;
	private int width, height;
	
	public String getTitle() {
		return title;
	}
	
	public Image setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getSrc() {
		return src;
	}
	
	public Image setSrc(String src) {
		this.src = src;
		return this;
	}
	
	public String getAlt() {
		return alt;
	}
	
	public Image setAlt(String alt) {
		this.alt = alt;
		return this;
	}
	
	public int getWidth() {
		return width;
	}
	
	public Image setWidth(int width) {
		this.width = width;
		return this;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Image setHeight(int height) {
		this.height = height;
		return this;
	}
}
