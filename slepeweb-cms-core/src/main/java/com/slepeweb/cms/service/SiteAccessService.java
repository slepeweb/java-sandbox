package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.bean.User;

public interface SiteAccessService {
	final static String LOGIN_PATH = "/login";
	final static String NOT_AUTHORISED_PATH = "/login/notauthorised";
	
	boolean isAccessible(Item i);
	boolean isAccessible(SolrDocument4Cms doc, User u);
}
