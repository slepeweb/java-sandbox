package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.LoginSupport;

import jakarta.servlet.http.HttpServletRequest;


public interface LoginService {
	LoginSupport login(String email, String password, HttpServletRequest req);
	LoginSupport login(String email, String password, boolean asContentEditor, HttpServletRequest req);
	void logout(HttpServletRequest req);
}
