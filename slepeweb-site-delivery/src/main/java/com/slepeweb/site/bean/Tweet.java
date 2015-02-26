package com.slepeweb.site.bean;

import java.util.Date;

import twitter4j.Status;

public class Tweet {

	private Date createdAt;
	private String text;
	private TwitterAccount account;

	public Tweet() {}
	
	public Tweet(Status status) {
		this.createdAt = status.getCreatedAt();
		this.text = status.getText();
	}

	public TimeAgo getTimeAgo() {
		return TimeAgo.getInstance(this.createdAt);
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public Tweet setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public String getText() {
		return this.text;
	}

	public Tweet setText(String text) {
		this.text = text;
		return this;
	}
	
	public String getMessage() {
		return String.format("%s: %s", this.account.getName(), getText());
	}

	public TwitterAccount getAccount() {
		return account;
	}

	public Tweet setAccount(TwitterAccount account) {
		this.account = account;
		return this;
	}


}