package com.slepeweb.sandbox.www.model;

public class LoginPage extends Page {
	private String nextView;

	public String getNextView() {
		return nextView;
	}

	public LoginPage setNextView(String nextView) {
		this.nextView = nextView;
		return this;
	}
}