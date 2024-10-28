package com.slepeweb.site.pho.bean;

import jakarta.servlet.http.HttpServletRequest;

public class PhoCookieValues {
	private String text, from, to;
	
	public PhoCookieValues() {}
	
	public PhoCookieValues(HttpServletRequest req) {
		this.text = req.getParameter("searchtext");
		this.from = req.getParameter("from");
		this.to = req.getParameter("to");
	}

	public String getText() {
		return text;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public PhoCookieValues setText(String text) {
		this.text = text;
		return this;
	}

	public PhoCookieValues setFrom(String from) {
		this.from = from;
		return this;
	}

	public PhoCookieValues setTo(String to) {
		this.to = to;
		return this;
	}
}
