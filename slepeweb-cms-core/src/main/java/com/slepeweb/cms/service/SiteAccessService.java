package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;

public interface SiteAccessService {
	final static String LOGIN_PATH = "/login";
	final static String NOT_AUTHORISED_PATH = "/login/notauthorised";
	
	boolean isAccessible(Item i);
	boolean isAccessible(Item i, String springTemplatePath);
}
