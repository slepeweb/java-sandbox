package com.slepeweb.site.bean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class UrlParser {

	private String hostname, port, protocol, path, minipath;
	private Map<String, String> queryParams = new HashMap<String, String>();

	public UrlParser parse(String url) {
		if (StringUtils.isNotBlank(url)) {
			String remainder = "";
			
			String[] parts = url.split("//");
			if (parts.length == 1) {
				this.hostname = null;
				this.protocol = null;
				remainder = url;
			}
			else if (parts.length == 2) {
				if (StringUtils.isNotBlank(parts[0])) {
					this.protocol = parts[0];
					if (this.protocol.endsWith(":")) {
						this.protocol = this.protocol.substring(0, this.protocol.length() - 1);
					}
				}
				
				int cursor = parts[1].indexOf("/");
				if (cursor > -1) {
					this.hostname = parts[1].substring(0, cursor);
					remainder = parts[1].substring(cursor);
				}
				else {
					remainder = "";
				}
			}
			
			parts = remainder.split("\\?"); 
			this.path = parts[0];
			parseQuery(parts.length == 1 ? null : parts[1]);
		}
		
		return this;
	}
	
	private void parseQuery(String queryStr) {
		String[] pair;
		
		if (queryStr != null) {
			for (String pairStr : queryStr.split("&")) {
				pair = pairStr.split("=");
				addQueryParam(pair[0], pair.length < 2 ? "" : pair[1]);
			}
		}
	}
	
	public UrlParser addQueryParam(String name, String value) {
		if (! this.queryParams.containsKey(name)) {
			this.queryParams.put(name, value);
		}
		return this;
	}
	
	public void removeQueryParam(String name) {
		this.queryParams.remove(name);
	}
	
	public String getQueryParam(String name) {
		return this.queryParams.get(name);
	}
	
	public boolean isQueryPresent() {
		return this.queryParams.size() > 0;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (this.protocol != null) {
			sb.append(this.protocol).append(":");
		}
		
		if (this.hostname != null) {
			sb.append("//").append(this.hostname);
		}
		
		if (StringUtils.isNotBlank(this.path)) { 
			sb.append(this.path);
		}
		
		sb.append(getQueryStr());		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(new UrlParser().parse("http://a.com/a/b/c?d=1&e=2"));
		System.out.println(new UrlParser().parse("http://a.com/a/b/c"));
		System.out.println(new UrlParser().parse("/a/b/c?d=1&e=2"));
		System.out.println(new UrlParser().parse("/a/b/c"));
		System.out.println(new UrlParser().parse("/"));
		System.out.println(new UrlParser().parse("//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"));
		System.out.println(new UrlParser().parse("0"));
		System.out.println(new UrlParser().parse("/$_1234").addQueryParam("static", "1"));
		System.out.println(new UrlParser().parse("/$_1234?view=thumb"));
		System.out.println(new UrlParser().parse(""));
	}
	
	public String getHostname() {
		return hostname;
	}

	public UrlParser setHostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	public String getPort() {
		return port;
	}

	public UrlParser setPort(String port) {
		this.port = port;
		return this;
	}

	public String getProtocol() {
		return protocol;
	}

	public UrlParser setProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	public String getPath() {
		return path;
	}

	public UrlParser setPath(String path) {
		this.path = path;
		return this;
	}

	public String getQueryStr() {
		if (isQueryPresent()) {
			Iterator<String> iter = this.queryParams.keySet().iterator();
			StringBuilder sb = new StringBuilder("?");
			String continuation = "";
			String name;
			
			while (iter.hasNext()) {
				name = iter.next();
				sb.append(continuation).append(name).append("=").append(this.queryParams.get(name));
			}
			
			return sb.toString();
		}
		
		return "";
	}

	public String getPathAndQuery() {
		return new StringBuilder(getPath()).append(getQueryStr()).toString();
	}

	public String getMinipathAndQuery() {
		return new StringBuilder(getMinipath()).append(getQueryStr()).toString();
	}

	public String getMinipath() {
		return minipath;
	}

	public void setMinipath(String minipath) {
		this.minipath = minipath;
	}
	
	public void setMinipath(long id) {
		this.minipath = String.format("/$_%d", id);
	}
}
