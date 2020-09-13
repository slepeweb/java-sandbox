package com.slepeweb.cms.bean;

public class LoginSupport {

	private User user;
	private boolean success;
	private String errorMessage;
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public LoginSupport setErrorMessage(String message) {
		this.errorMessage = message;
		return this;
	}

	public User getUser() {
		return user;
	}
	
	public LoginSupport setUser(User user) {
		this.user = user;
		return this;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public LoginSupport setSuccess(boolean status) {
		this.success = status;
		return this;
	}
}
