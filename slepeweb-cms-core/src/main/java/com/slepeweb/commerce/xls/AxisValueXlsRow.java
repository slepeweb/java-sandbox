package com.slepeweb.commerce.xls;

public class AxisValueXlsRow {
	private String update, axis, value;

	public String getAxis() {
		return axis;
	}

	public void setAxis(String a) {
		this.axis = a;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String v) {
		this.value = v;
	}

	@Override
	public String toString() {
		return getAxis();
	}
	
	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

}
