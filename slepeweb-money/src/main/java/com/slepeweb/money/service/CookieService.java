package com.slepeweb.money.service;

import java.sql.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
	void updateAccountCookie(long accountId, HttpServletRequest req, HttpServletResponse res);
	Long getAccountId(HttpServletRequest req);
	void updateLastEnteredCookie(Date t, HttpServletRequest req, HttpServletResponse res);
	Date getLastEntered(HttpServletRequest req);
}
