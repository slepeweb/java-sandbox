package com.slepeweb.site.service;

public interface PasskeyService {
	String issueKey();
	boolean validateKey(String s);
}
