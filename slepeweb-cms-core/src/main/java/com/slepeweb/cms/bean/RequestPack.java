package com.slepeweb.cms.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.component.Passkey;
import com.slepeweb.cms.constant.AttrName;

import jakarta.servlet.http.HttpServletRequest;

public class RequestPack {

	private String language = "en";
	private User user;
	private HttpServletRequest httpRequest;
	private Map<String, String[]> params = new HashMap<String, String[]>();
		
	public RequestPack(HttpServletRequest req) {
		this.httpRequest = req;
		this.user = (User) req.getSession().getAttribute(AttrName.USER);
		this.params = req.getParameterMap();
	}
	
	public Map<String, String[]> getParams() {
		return params;
	}

	public RequestPack setParams(Map<String, String[]> params) {
		this.params = params;
		return this;
	}
	
	public User getUser() {
		return user;
	}
	
	public RequestPack setUser(User user) {
		this.user = user;
		this.httpRequest.getSession().setAttribute(AttrName.USER, user);
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public RequestPack setLanguage(String language) {
		this.language = language;
		return this;
	}

	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	public Passkey getPasskey() {
		String passkey = getParameter(AttrName.PASSKEY);
		if (StringUtils.isNotBlank(passkey)) {
			return new Passkey(passkey);
		}
		return null;
	}
	
	public boolean hasPasskey() {
		return getPasskey() != null;
	}
	
	private String getParameter(String name) {
		String[] values = (String[]) this.params.get(name);
		if (values != null && values.length > 0) {
			return values[0];
		}
		
		return null;
	}
}
