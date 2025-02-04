package com.slepeweb.money.bean;

public class LoginResponse {

	private User user;
	private boolean success;
	private String errorMessage;
	private boolean sendEmail = false;
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public LoginResponse setErrorMessage(String message) {
		this.errorMessage = message;
		return this;
	}

	public User getUser() {
		return user;
	}
	
	public LoginResponse setUser(User user) {
		this.user = user;
		return this;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public LoginResponse setSuccess(boolean status) {
		this.success = status;
		return this;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	public LoginResponse setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
		return this;
	}
}
