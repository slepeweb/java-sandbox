package com.slepeweb.site.bean;

public class TwitterAccount {

	private String name, iconPath;
	private int numTweets;

	public String getName() {
		return name;
	}

	public TwitterAccount setName(String name) {
		this.name = name;
		return this;
	}

	public String getIconPath() {
		return iconPath;
	}

	public TwitterAccount setIconPath(String thumbnailSrc) {
		this.iconPath = thumbnailSrc;
		return this;
	}

	public int getNumTweets() {
		return numTweets;
	}

	public TwitterAccount setNumTweets(int numTweets) {
		this.numTweets = numTweets;
		return this;
	}
		
}
