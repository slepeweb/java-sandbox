package com.slepeweb.site.pho.bean;

import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public class PhoCookieValues {
	private String text, from, to;
	
	public PhoCookieValues() {
		this.text = this.from = this.to = "";
	}
	
	public PhoCookieValues(String cookieValueStr) {
		String[] parts = cookieValueStr.split("[\\|]");
		this.text = dash2Empty(parts[0]);
		this.from = dash2Empty(parts[1]);
		this.to = dash2Empty(parts[2]);
	}
	
	public PhoCookieValues(HttpServletRequest req) {
		this.text = req.getParameter("searchtext");
		this.from = req.getParameter("from");
		this.to = req.getParameter("to");
	}
	
	@Override
	public String toString() {
		return String.format("%s|%s|%s", empty2Dash(this.text), empty2Dash(this.from), empty2Dash(this.to));
	}
	
	private String empty2Dash(String s) {
		return StringUtils.isBlank(s) ? "-" : s;
	}
	
	private String dash2Empty(String s) {
		return s.equals("-") ? "" : s;
	}
	
	public String getText() {
		return this.text;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public PhoCookieValues setText(String s) {
		this.text = s;
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
