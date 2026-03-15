package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;

public class LinkTarget implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title, teaser, href, style, fontAwesomeClass;
	private Image thumbnail;
	private boolean trusted, selected, foreigner;
	private List<LinkTarget> children = new ArrayList<LinkTarget>();
	
	public LinkTarget() {}
	
	public LinkTarget(Item i) {
		String s = i.getFieldValue("navtitle");
		if (StringUtils.isBlank(s)) {
			s = i.getTitle();
		}
		
		setTitle(s);
		setTeaser(i.getFieldValue("teaser"));
		setFontAwesomeClass(i.getType().getFontAwesomeClass());
		setHref(i.isForeigner() ? i.getMiniPath() : i.getUrl());
		setForeigner(i.isForeigner());
	}
	
	public String toString() {
		return getHref();
	}
	
	public String getTag() {
		StringBuffer sb = new StringBuffer(String.format("<a href=\"%s\"", getHref()));
		
		if (StringUtils.isNotBlank(getStyle())) {
			sb.append(String.format(" class=\"%s\"", getStyle()));
		}
		
		sb.append(String.format(">%s</a>", getTitle()));
		return sb.toString();
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

	public String getFontAwesomeClass() {
		return fontAwesomeClass;
	}

	public LinkTarget setFontAwesomeClass(String fontAwesomeClass) {
		this.fontAwesomeClass = fontAwesomeClass;
		return this;
	}

	public boolean isForeigner() {
		return foreigner;
	}

	public LinkTarget setForeigner(boolean foreigner) {
		this.foreigner = foreigner;
		return this;
	}
}
