package com.slepeweb.cms.bean;

import java.util.HashMap;
import java.util.Map;

import com.slepeweb.cms.constant.AttrName;

public class Request {

	private User user;
	private Map<String, String[]> params = new HashMap<String, String[]>();;
	
	public Map<String, String[]> getParams() {
		return params;
	}

	public void setParams(Map<String, String[]> params) {
		this.params = params;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getPasskey() {
		return getParameter(AttrName.PASSKEY);
	}
	
	public boolean isPasskey() {
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
