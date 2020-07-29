package com.slepeweb.cms.bean;

public class Redirector {

	private String path, language;
	private boolean required;
	
	public String getPath() {
		return path;
	}

	public boolean isRequired() {
		return required;
	}

	public String getLanguage() {
		return language;
	}

	public Redirector setLanguage(String language) {
		this.language = language;
		return this;
	}

	public Redirector setPath(String url) {
		this.path = url;
		return this;
	}

	public Redirector setRequired(boolean required) {
		this.required = required;
		return this;
	}
}
