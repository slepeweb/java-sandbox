package com.slepeweb.site.bean;

public class TwitterAccount {

	private String name, iconPath;

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
		
}
