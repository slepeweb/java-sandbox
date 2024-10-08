package com.slepeweb.site.model;

import java.io.Serializable;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.StringWrapper;

public class Image implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String BACKGROUND = "background";
	
	public enum Align {left, centre, right}
	
	private String title, src, alt, type;
	private int width, height;
	private Align align = Align.left;
	
	public Image() {}
	
	public Image(Link l) {
		this(l.getChild(), l.getName());
	}
	
	public Image(Item i, String type) {
		setTitle(i.getFieldValue("title", new StringWrapper("")));
		setSrc(i.getPath());
		setAlt(getTitle());
		setType(type);
	}
	
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

	public String getType() {
		return type;
	}

	public Image setType(String type) {
		this.type = type;
		return this;
	}

	public Align getAlign() {
		return align;
	}

	public Image setAlign(Align align) {
		this.align = align;
		return this;
	}
}
