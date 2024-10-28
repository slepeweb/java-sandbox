package com.slepeweb.money.service;

import java.sql.Timestamp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
	void updateAccountCookie(long accountId, HttpServletRequest req, HttpServletResponse res);
	Long getAccountId(HttpServletRequest req);
	void updateLastEnteredCookie(Timestamp t, HttpServletRequest req, HttpServletResponse res);
	Timestamp getLastEntered(HttpServletRequest req);
}
