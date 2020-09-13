package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.User;

public interface SiteAccessService {
	final static String LOGIN_PATH = "/login";
	final static String NOT_AUTHORISED_PATH = "/login/notauthorised";
	
	boolean hasReadAccess(Item i, String springTemplatePath, User u);
	boolean hasWriteAccess(Item i, User u);
}
