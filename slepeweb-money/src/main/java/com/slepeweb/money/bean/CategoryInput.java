package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({"ready"})
public class CategoryInput extends Category {
	
	private List<String> options = new ArrayList<String>();
	
	public List<String> getOptions() {
		return options;
	}
	
	public CategoryInput setOptions(List<String> options) {
		this.options = options;
		return this;
	}

	public CategoryInput setId(long id) {
		super.setId(id);
		return this;
	}
	
	public CategoryInput setOrigId(long origId) {
		super.setOrigId(origId);
		return this;
	}

	public CategoryInput setMajor(String name) {
		super.setMajor(name);
		return this;
	}

	public CategoryInput setMinor(String minor) {
		super.setMinor(minor);
		return this;
	}
	
	public CategoryInput setExclude(boolean exclude) {
		super.setExclude(exclude);
		return this;
	}
	
	public boolean isReady() {
		return StringUtils.isNotBlank(getMajor());
	}
}
