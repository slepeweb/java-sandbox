package com.slepeweb.sandbox.www.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.slepeweb.sandbox.www.model.Page;

@Service
public class PageServiceImpl implements PageService {
	private static Logger LOG = Logger.getLogger(PageService.class);
	
	/*
	 * DATA format is a list of:
	 * {href, title, view, css, js, roles}
	 */
	private static final String[][] DATA = {
		{ ABOUT, "About", "home", "slepeweb", "", ""},
		{ PROFILE, "Profile", "projects", "slepeweb", "", ""},
		{ CONTACT, "Contact us", "contact", "slepeweb", "", ""},
		{ SANDBOX_PLATFORM, "Sandbox platform", "sandbox.platform", "slepeweb", "sandbox", ""},
		{ SANDBOX_WS, "Sandbox web services", "sandbox.ws", "slepeweb", "sandbox", ""},
		{ USER_INTRO, "Users - introduction", "sandbox.intro", "slepeweb", "", ""},
		{ USER_ADD, "Users - add user", "sandbox.userForm", "slepeweb", "", ""},
		{ USER_LIST, "Users - list users", "sandbox.userList", "slepeweb", "", ""},
		{ USER_UPDATE, "Users - update user", "sandbox.userForm", "slepeweb", "", ""},
		{ SPIZZA, "Spizza", "spizza.welcome", "slepeweb", "", ""},
	};
		
	private Map<String, Page> MAP = new HashMap<String, Page>();

	@PostConstruct
	public void init() {
		Page p;
		
		for (String[] data : DATA) {
			if (data.length == 6) {
				p = new Page().setHref(data[0]).setTitle(data[1]).setView(data[2]);

				for (String css : data[3].split(",")) {
					if (StringUtils.isNotBlank(css)) {
						p.addStylesheet(String.format("/resources/css/%s.css", css.trim()));
					}
				}
				
				for (String js : data[4].split(",")) {
					if (StringUtils.isNotBlank(js)) {
						p.addJavascript(String.format("/resources/js/%s.js", js.trim()));	
					}
				}
				
				for (String role : data[5].split(",")) {
					if (StringUtils.isNotBlank(role)) {
						p.addRole(role.trim());		
					}
				}
				
				MAP.put(p.getHref(), p);
				LOG.info(String.format("Defined page [%s]", p.getHref()));
			}
		}
	}
	
	public Page getPage(String url) {
		return MAP.get(url);
	}

}
