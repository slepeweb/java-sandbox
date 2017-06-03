package com.slepeweb.commerce.bean;

public class AxisValue {
	
	private Long id, axisId;
	private int ordering;
	private String value;
	
	public Long getId() {
		return id;
	}
	
	public AxisValue setId(Long id) {
		this.id = id;
		return this;
	}
	
	public Long getAxisId() {
		return axisId;
	}
	
	public AxisValue setAxisId(Long axisId) {
		this.axisId = axisId;
		return this;
	}
	
	public int getOrdering() {
		return ordering;
	}
	
	public AxisValue setOrdering(int ordering) {
		this.ordering = ordering;
		return this;
	}
	
	public String getValue() {
		return value;
	}
	
	public AxisValue setValue(String value) {
		this.value = value;
		return this;
	}	
}
