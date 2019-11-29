package com.slepeweb.ifttt.bean;

public class Source {
	private String id = "2", url = "https://ifttt.com/myrecipes/personal/2";

	public String getId() {
		return id;
	}

	public Source setId(String id) {
		this.id = id;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public Source setUrl(String url) {
		this.url = url;
		return this;
	}
}
