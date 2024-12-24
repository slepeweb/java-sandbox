package com.slepeweb.cms.service;

public interface PasskeyService {
	String issueKey();
	boolean validateKey(String s);
}
