package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Rerouter {

	private List <String> messages = new ArrayList<String>();
	private Action action;
	private int httpError;
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

	public Rerouter setLanguage(String language) {
		this.language = language;
		return this;
	}

	public Rerouter setPath(String url) {
		this.path = url;
		return this;
	}

	public Rerouter setRequired(boolean required) {
		this.required = required;
		return this;
	}
	
	public Action getAction() {
		return action;
	}

	public Rerouter setAction(Action action) {
		this.action = action;
		return this;
	}

	public int getHttpError() {
		return httpError;
	}

	public Rerouter setHttpError(int httpError) {
		this.httpError = httpError;
		return this;
	}

	public String getMessage() {
		return StringUtils.join(this.messages, "...");
	}
	
	public List<String> getMessages() {
		return messages;
	}
	
	public Rerouter addMessage(String s) {
		this.messages.add(s);
		return this;
	}
	
	public enum Action {
		redirect, error
	}
}
