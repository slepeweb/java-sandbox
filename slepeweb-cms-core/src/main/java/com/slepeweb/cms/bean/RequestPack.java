package com.slepeweb.cms.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.component.Passkey;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.common.util.HttpUtil;

import jakarta.servlet.http.HttpServletRequest;

public class RequestPack {

	private String language = "en";
	private User user;
	private Site site;
	private HttpServletRequest httpRequest;
	private Map<String, String[]> params = new HashMap<String, String[]>();
	private boolean miniPath;
	private String view;
	
	/*
	 *  Multilingulal sites have url's the begin with the 2-character country code, 
	 *  eg. /gr/item/path. In this example, the requestPath would be '/gr/item/path',
	 *  the country code would be 'gr', and the itemPath would be '/item/path'.
	 */
	private String itemPath, requestPath;
	
	public RequestPack(User u) {
		this.user = u;
	}
	
	public RequestPack(HttpServletRequest req) {
		this.httpRequest = req;
		this.user = (User) req.getSession().getAttribute(AttrName.USER);
		this.params = req.getParameterMap();		
		this.view = getParameter("view");
	}
	
	public Map<String, String[]> getParams() {
		return params;
	}

	public RequestPack setParams(Map<String, String[]> params) {
		this.params = params;
		return this;
	}
	
	public String getQueryString() {

		if (! this.params.isEmpty()) {
			StringBuffer sb = new StringBuffer("?");
			String continuation = "";
			String[] values;
			
			for (String name : this.params.keySet()) {
				values = this.params.get(name);
				for (int i = 0; i < values.length; i++) {
					sb.append(String.format("%s%s=%s", continuation, name, HttpUtil.encodeUrl(values[i])));
					continuation = "&";
				}
			}
			
			return sb.toString();
		}
		
		return "";
	}
	
	public User getUser() {
		return user;
	}
	
	public RequestPack setUser(User user) {
		this.user = user;
		if (this.httpRequest != null) {
			this.httpRequest.getSession().setAttribute(AttrName.USER, user);
		}
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
	
	public RequestPack setMiniPath(boolean b) {
		this.miniPath = b;
		return this;
	}

	public boolean isMiniPath() {
		return this.miniPath;
	}

	public Site getSite() {
		return site;
	}

	public RequestPack setSite(Site site) {
		this.site = site;
		return this;
	}

	public String getItemPath() {
		return itemPath;
	}

	public RequestPack setItemPath(String path) {
		this.itemPath = path;
		return this;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public RequestPack setRequestPath(String requestPath) {
		this.requestPath = requestPath;
		return this;
	}

	private String getParameter(String name) {
		String[] values = (String[]) this.params.get(name);
		if (values != null && values.length > 0) {
			return values[0];
		}
		
		return null;
	}

	public String getView() {
		return view;
	}
}
