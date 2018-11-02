package com.slepeweb.money.bean;

public class Option {

	private String name;
	private int value;
	private boolean selected;
	
	public Option(int value, String name) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
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
