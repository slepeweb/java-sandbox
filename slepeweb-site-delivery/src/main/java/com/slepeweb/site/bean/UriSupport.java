package com.slepeweb.site.bean;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class UriSupport {

	private URI uri;
	private String protocol, host, path, minipath, fragment;
	private int port;
	private Map<String, String> queryParams = new HashMap<String, String>();

	
	public UriSupport(String urlStr) {
		this.uri = URI.create(urlStr);
		this.protocol = this.uri.getScheme();
		this.host = this.uri.getHost();
		this.port = this.uri.getPort();
		this.path = this.uri.getPath();
		this.fragment = this.uri.getFragment();
		
		mapQuery(this.uri.getQuery());
	}
	
	private void mapQuery(String queryStr) {
		String[] pair;
		
		if (queryStr != null) {
			for (String pairStr : queryStr.split("&")) {
				pair = pairStr.split("=");
				addQueryParam(pair[0], pair.length < 2 ? "" : pair[1]);
			}
		}
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

	
	public UriSupport addQueryParam(String name, String value) {
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
		
		if (this.host != null) {
			sb.append("//").append(this.host);
		}
		
		if (StringUtils.isNotBlank(this.path)) { 
			sb.append(this.path);
		}
		
		String s = getQueryStr();
		if (StringUtils.isNotBlank(s)) { 
			sb.append(s);
		}
		
		if (StringUtils.isNotBlank(this.fragment)) { 
			sb.append("#").append(this.fragment);
		}

		return sb.toString();
	}
	
	public String getProtocol() {
		return this.protocol;
	}

	public UriSupport setProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	public String getHost() {
		return this.host;
	}

	public UriSupport setHost(String hostname) {
		this.host = hostname;
		return this;
	}

	public int getPort() {
		return this.port;
	}

	public UriSupport setPort(int port) {
		this.port = port;
		return this;
	}

	public String getPath() {
		return this.path;
	}

	public UriSupport setPath(String path) {
		this.path = path;
		return this;
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
	
	public static void main(String[] args) {
		System.out.println(new UriSupport("http://a.com/a/b/c?d=1&e=2"));
		System.out.println(new UriSupport("http://a.com/a/b/c"));
		System.out.println(new UriSupport("/a/b/c?d=1&e=2"));
		System.out.println(new UriSupport("/a/b/c"));
		System.out.println(new UriSupport("/"));
		System.out.println(new UriSupport("//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"));
		System.out.println(new UriSupport("0"));
		System.out.println(new UriSupport("/$_1234#honds250").addQueryParam("static", "1"));
		System.out.println(new UriSupport("/$_1234?view=thumb"));
		System.out.println(new UriSupport(""));
	}
	
}
