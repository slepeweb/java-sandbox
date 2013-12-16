package com.slepeweb.sandbox.www.service;

import com.slepeweb.sandbox.www.model.Page;

public interface PageService {
	static final String ABOUT = "/about";
	static final String PROFILE = "/profile";
	static final String CONTACT = "/contact";
	static final String SANDBOX_PLATFORM = "/sandbox/platform";
	static final String SANDBOX_WS = "/sandbox/ws";
	static final String USER_INTRO = "/sandbox/user/intro";
	static final String USER_ADD = "/sandbox/user/add";
	static final String USER_LIST = "/sandbox/user/list";
	static final String USER_UPDATE = "/sandbox/user/update";
	static final String SPIZZA = "/sandbox/spizza";
	
	Page getPage(String url);
}
