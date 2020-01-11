package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;

public class LinkTarget implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title, teaser, href, style;
	private Image thumbnail;
	private boolean trusted, selected;
	private List<LinkTarget> children = new ArrayList<LinkTarget>();
	
	public LinkTarget() {}
	
	public LinkTarget(Item i) {
		setTitle(StringUtils.isBlank(i.getFieldValue("navtitle")) ? 
				i.getFieldValue("title") : i.getFieldValue("navtitle"));
		setTeaser(i.getFieldValue("teaser"));
		setHref(i.getUrl());
	}
	
	public String toString() {
		return getHref();
	}
	
	public String getTitle() {
		return title;
	}
	
	public LinkTarget setTitle(String label) {
		this.title = label;
		return this;
	}
	
	public String getHref() {
		return href;
	}
	
	public LinkTarget setHref(String href) {
		this.href = href;
		return this;
	}
	
	public Image getThumbnail() {
		return thumbnail;
	}
	
	public LinkTarget setThumbnail(Image thumbnail) {
		this.thumbnail = thumbnail;
		return this;
	}
	
	public boolean isTrusted() {
		return trusted;
	}
	
	public LinkTarget setTrusted(boolean trusted) {
		this.trusted = trusted;
		return this;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public LinkTarget setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	public List<LinkTarget> getChildren() {
		return children;
	}
	
	public void setChildren(List<LinkTarget> children) {
		this.children = children;
	}

	public String getTeaser() {
		return teaser;
	}

	public LinkTarget setTeaser(String teaser) {
		this.teaser = teaser;
		return this;
	}

	public String getStyle() {
		return style;
	}

	public LinkTarget setStyle(String style) {
		this.style = style;
		return this;
	}
}
