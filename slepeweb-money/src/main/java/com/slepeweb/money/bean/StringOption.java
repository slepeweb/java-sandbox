package com.slepeweb.money.bean;

public class StringOption {

	private String name;
	private String value;
	private boolean selected;
	
	public StringOption() {}
	
	public StringOption(String value, String name) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return String.format("%s/%d", getName(), getValue());
	}
}
