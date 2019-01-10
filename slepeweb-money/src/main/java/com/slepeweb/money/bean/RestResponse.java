package com.slepeweb.money.bean;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class RestResponse {

	private List <String> messages = new ArrayList<String>();
	private boolean error;
	private Object data;
	
	public Object getData() {
		return data;
	}

	public RestResponse setData(Object data) {
		this.data = data;
		return this;
	}

	public String getMessage() {
		return StringUtils.join(this.messages, "|");
	}
	
	public RestResponse parseMessages(String s) {
		this.messages = Arrays.asList(s.split("\\|"));
		return this;
	}
	
	public String getMessageEncoded() {
		try {
			return URLEncoder.encode(getMessage(), "utf-8");
		}
		catch (Exception e) {
			return "no_message";
		}
	}
	
	public List<String> getMessages() {
		return messages;
	}
	
	public RestResponse addMessage(String s) {
		this.messages.add(s);
		return this;
	}
	
	public boolean isError() {
		return error;
	}
	
	public RestResponse setError(boolean error) {
		this.error = error;
		return this;
	}
	
	
}
