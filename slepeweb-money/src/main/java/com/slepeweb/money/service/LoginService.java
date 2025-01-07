package com.slepeweb.money.service;

import com.slepeweb.money.bean.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface LoginService {
	LoginResponse login(String email, String password);
	void logout(HttpServletRequest req);
}
