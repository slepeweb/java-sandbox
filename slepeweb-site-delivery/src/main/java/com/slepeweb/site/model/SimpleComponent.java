package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.site.util.StringUtil;

public class SimpleComponent implements NestableComponent, Serializable {
	private static final long serialVersionUID = 1L;
	private String name, heading, blurb;
	private String type, view;
	private String cssClass, js;
	private List<SimpleComponent> components = new ArrayList<SimpleComponent>();

	public SimpleComponent setup(Link l) {
		setView(StringUtil.toIdentifier(l.getName()));
		setType(l.getChild());
		setCssClass(l.getChild().getFieldValue("css"));	
		setJs(l.getChild().getFieldValue("js"));
		setHeading(l.getChild().getFieldValue("heading"));
		setBlurb(l.getChild().getFieldValue("blurb"));
		setName(l.getChild().getName());
		return this;
	}
	
	public String toString() {
		return String.format("SimpleComponent (%s): %s", getType(), getHeading());
	}
	
	public String getView() {
		return view;
	}

	public SimpleComponent setView(String view) {
		this.view = view;
		return this;
	}

	public List<SimpleComponent> getComponents() {
		return components;
	}

	public void setComponents(List<SimpleComponent> subComponents) {
		this.components = subComponents;
	}

	public String getType() {
		return type;
	}

	public SimpleComponent setType(String type) {
		this.type = type;
		return this;
	}
	
	public SimpleComponent setType(Item i) {
		String linkedItemType = i.getType().getName();
		setType(StringUtil.toIdentifier(linkedItemType.equals("Component") ?
							i.getFieldValue("component-type") : linkedItemType));
		return this;
	}

	public String getCssClass() {
		return cssClass;
	}

	public SimpleComponent setCssClass(String cssClass) {
		this.cssClass = cssClass;
		return this;
	}

	public String getHeading() {
		return heading;
	}

	public SimpleComponent setHeading(String heading) {
		this.heading = heading;
		return this;
	}

	public String getBlurb() {
		return blurb;
	}

	public SimpleComponent setBlurb(String blurb) {
		this.blurb = blurb;
		return this;
	}

	public String getName() {
		return name;
	}

	public SimpleComponent setName(String name) {
		this.name = name;
		return this;
	}

	public String getJs() {
		return js;
	}

	public void setJs(String js) {
		this.js = js;
	}

}
