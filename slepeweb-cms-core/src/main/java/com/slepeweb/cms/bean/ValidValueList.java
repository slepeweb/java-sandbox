package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;

public class ValidValueList {
	private List<String> values = new ArrayList<String>();;
	private String defaultValue;
	
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public void addValue(String s) {
		getValues().add(s);
	}
}
