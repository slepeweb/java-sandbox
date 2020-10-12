package com.slepeweb.cms.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Url {

	private String hostname, port, protocol, path, query;
	private Map<String, String> queryParams;

	public Url parse(String u) {
		if (StringUtils.isNotBlank(u)) {
			String remainder = "";
			
			String[] parts = u.split("//");
			if (parts.length == 1) {
				this.hostname = null;
				this.protocol = null;
				remainder = u;
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
			this.query = parts.length == 1 ? null : parts[1];
			parseQuery();
		}
		
		return this;
	}
	
	private void parseQuery() {
		this.queryParams = new HashMap<String, String>();
		String[] pair;
		
		if (this.query != null) {
			for (String pairStr : this.query.split("&")) {
				pair = pairStr.split("=");
				this.queryParams.put(pair[0], pair.length < 2 ? "" : pair[1]);
			}
		}
	}
	
	public String getQueryParam(String name) {
		return this.queryParams.get(name);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (this.protocol != null) {
			sb.append(this.protocol).append(":");
		}
		
		if (this.hostname != null) {
			sb.append("//").append(this.hostname);
		}
		
		sb.append(this.path);
		
		if (this.query != null) {
			sb.append("?").append(this.query);
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(new Url().parse("http://a.com/a/b/c?d=1&e=2"));
		System.out.println(new Url().parse("http://a.com/a/b/c"));
		System.out.println(new Url().parse("/a/b/c?d=1&e=2"));
		System.out.println(new Url().parse("/a/b/c"));
		System.out.println(new Url().parse("/"));
		System.out.println(new Url().parse("//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"));
	}
	
	public String getHostname() {
		return hostname;
	}

	public Url setHostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	public String getPort() {
		return port;
	}

	public Url setPort(String port) {
		this.port = port;
		return this;
	}

	public String getProtocol() {
		return protocol;
	}

	public Url setProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	public String getPath() {
		return path;
	}

	public Url setPath(String path) {
		this.path = path;
		return this;
	}

	public String getQuery() {
		return query;
	}

	public Url setQuery(String query) {
		this.query = query;
		return this;
	}
	
	public String getPathAndQuery() {
		StringBuilder sb = new StringBuilder(getPath());
		if (getQuery() != null) {
			sb.append("?").append(this.query);
		}
		return sb.toString();
	}
}
