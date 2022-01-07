package com.slepeweb.cms.bean;

import java.util.List;

public class TagInputSupport {
	private List<String> recent, all;

	public List<String> getRecent() {
		return recent;
	}

	public TagInputSupport setRecent(List<String> recent) {
		this.recent = recent;
		return this;
	}

	public List<String> getAll() {
		return all;
	}

	public TagInputSupport setAll(List<String> all) {
		this.all = all;
		return this;
	}
}
