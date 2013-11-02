package com.slepeweb.sandbox.www.model;

public class LoginPage extends Page {
	private String nextPath;

	public String getNextPath() {
		return nextPath;
	}

	public LoginPage setNextPath(String nextPath) {
		this.nextPath = nextPath;
		return this;
	}
}
