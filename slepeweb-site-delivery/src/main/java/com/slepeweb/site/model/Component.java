package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.site.util.StringUtil;

public class Component implements ComponentContainer, Serializable {
	private static final long serialVersionUID = 1L;
	private String type, heading, blurb, view;
	private List<Component> components;
	private List<Link> media;

	public String getView() {
		return view;
	}

	public Component setView(String view) {
		this.view = view;
		return this;
	}

	public String getHeading() {
		return heading;
	}

	public Component setHeading(String heading) {
		this.heading = heading;
		return this;
	}

	public String getBlurb() {
		return blurb;
	}

	public Component setBlurb(String blurb) {
		this.blurb = blurb;
		return this;
	}

	public List<Component> getComponents() {
		return components;
	}

	public Component setComponents(List<Component> subComponents) {
		this.components = subComponents;
		return this;
	}

	public List<Link> getMedia() {
		return media;
	}

	public Component setMedia(List<Link> media) {
		this.media = media;
		return this;
	}

	public String getType() {
		return type;
	}

	public Component setType(String type) {
		this.type = type;
		return this;
	}
	
	public Component setType(Item i) {
		String linkedItemType = i.getType().getName();
		setType(StringUtil.toIdentifier(linkedItemType.equals("Component") ?
							i.getFieldValue("component-type") : linkedItemType));

		return this;
	}

}
