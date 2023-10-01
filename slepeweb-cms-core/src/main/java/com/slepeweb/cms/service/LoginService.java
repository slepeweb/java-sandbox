package com.slepeweb.cms.service;

import javax.servlet.http.HttpServletRequest;

import com.slepeweb.cms.bean.LoginSupport;


public interface LoginService {
	LoginSupport login(String email, String password, HttpServletRequest req);
	LoginSupport login(String email, String password, boolean asContentEditor, HttpServletRequest req);
	void logout(HttpServletRequest req);
}
