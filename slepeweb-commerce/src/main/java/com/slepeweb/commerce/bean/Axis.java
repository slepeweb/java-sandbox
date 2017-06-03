package com.slepeweb.commerce.bean;

public class Axis {
	private Long id;
	private String label, units, description;
	
	public Long getId() {
		return id;
	}
	
	public Axis setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getLabel() {
		return label;
	}
	
	public Axis setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public String getUnits() {
		return units;
	}
	
	public Axis setUnits(String units) {
		this.units = units;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Axis setDescription(String description) {
		this.description = description;
		return this;
	}
}
