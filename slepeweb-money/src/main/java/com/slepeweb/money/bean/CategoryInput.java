package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"ready", "defined4Insert", "inDatabase", "legacy", "allMajors", "allMinors", "visible", "lastVisible"})
public class CategoryInput extends Category {
	
	private List<String> allMajors = new ArrayList<String>();
	private List<String> allMinors = new ArrayList<String>();
	private List<String> options = new ArrayList<String>();
	private boolean visible, lastVisible;
	
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

	public List<String> getAllMajors() {
		return allMajors;
	}

	public CategoryInput setAllMajors(List<String> allMajors) {
		this.allMajors = allMajors;
		return this;
	}

	public List<String> getAllMinors() {
		return allMinors;
	}

	public CategoryInput setAllMinors(List<String> allMinors) {
		this.allMinors = allMinors;
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public CategoryInput setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public boolean isLastVisible() {
		return lastVisible;
	}

	public CategoryInput setLastVisible(boolean lastVisible) {
		this.lastVisible = lastVisible;
		return this;
	}
}
