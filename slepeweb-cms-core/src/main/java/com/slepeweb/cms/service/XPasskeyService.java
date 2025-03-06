package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.User;

public interface XPasskeyService {
	String issueKey(User u);
	User identifyUser(String s);
}
