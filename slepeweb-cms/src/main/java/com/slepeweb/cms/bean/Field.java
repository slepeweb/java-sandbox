package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;


public class Field {
	private Long id;
	private String name, variable, help;
	private FieldType type;
	private int size;
	
	public enum FieldType {
		text, markup, integer, date, url;
	}

	public void assimilate(Field f) {
		setName(f.getName());
		setVariable(f.getVariable());
		setHelp(f.getHelp());
		setType(f.getType());
		setSize(f.getSize());
	}

	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getVariable()) &&
			getType() != null;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
