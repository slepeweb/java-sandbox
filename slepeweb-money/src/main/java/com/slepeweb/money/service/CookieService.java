package com.slepeweb.money.service;

import java.time.LocalDate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
	void updateAccountCookie(long accountId, HttpServletRequest req, HttpServletResponse res);
	Long getAccountId(HttpServletRequest req);
	void updateLastEnteredCookie(LocalDate t, HttpServletRequest req, HttpServletResponse res);
	LocalDate getLastEntered(HttpServletRequest req);
}
