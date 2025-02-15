package com.slepeweb.cms.bean;

public class LoginSupport {

	private User user;
	private boolean success, sendmailFlag;
	private String alias, password, userMessage, emailMessage;
	
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

	public boolean isSendmailFlag() {
		return sendmailFlag;
	}

	public LoginSupport setSendmailFlag(boolean sendmailFlag) {
		this.sendmailFlag = sendmailFlag;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public LoginSupport setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public LoginSupport setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public LoginSupport setUserMessage(String message) {
		this.userMessage = message;
		return this;
	}

	public String getEmailMessage() {
		return emailMessage;
	}

	public LoginSupport setEmailMessage(String emailMessage) {
		this.emailMessage = emailMessage;
		return this;
	}

}
