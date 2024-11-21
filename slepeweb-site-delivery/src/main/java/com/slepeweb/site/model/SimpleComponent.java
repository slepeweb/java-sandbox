package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.StringWrapper;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.site.service.ComponentService;
import com.slepeweb.site.util.StringUtil;

public class SimpleComponent implements NestableComponent, Serializable {
	private static final long serialVersionUID = 1L;
	private String heading, body;
	private String type;
	private String cssClass, js, data;
	private Long id;
	private String identifier;
	private List<SimpleComponent> components;
	private ComponentService componentService;

	public SimpleComponent setup(Link l) {
		Item i = l.getChild();
		setId(i.getId());
		setType(i);
		setCssClass(i.getFieldValue(FieldName.CSS));	
		setJs(i.getFieldValue(FieldName.JS));
		setData(i.getFieldValue(FieldName.DATA));
		setHeading(i.getFieldValue(FieldName.HEADING));
		setBody(i.getFieldValueResolved(FieldName.BODYTEXT, new StringWrapper("")));
		setIdentifier(i.getFieldValue("identifier"));

		
		if (this.componentService != null) {
			this.components = this.componentService.getComponents(i.getComponents());
		}
		
		return this;
	}
	
	public String toString() {
		return String.format("%s (%s): %s", getClass().getTypeName(), getType(), getHeading());
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
		this.type = StringUtil.toIdentifier(type);
		return this;
	}
	
	public SimpleComponent setType(Item i) {
		setType(getComponentType(i)[1]);
		return this;
	}
	
	public static String[] getComponentType(Item i) {
		String itemType = i.getType().getName();

		if (itemType.equals(ItemTypeName.COMPONENT)) {
			return new String[] {"com.slepeweb.site.model.SimpleComponent", i.getFieldValue("component-type")};
		}

		return new String[] {"com.slepeweb.site.model." + StringUtils.capitalize(itemType) + "Component", itemType};
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

	public String getBody() {
		return body;
	}

	public SimpleComponent setBody(String blurb) {
		this.body = blurb;
		return this;
	}

	public String getJs() {
		return js;
	}

	public void setJs(String js) {
		this.js = js;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdentifier() {
		return StringUtils.isNotBlank(this.identifier) ? this.identifier : getHeading();
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public ComponentService getComponentService() {
		return componentService;
	}

	public SimpleComponent setComponentService(ComponentService componentService) {
		this.componentService = componentService;
		return this;
	}

}
