package com.slepeweb.site.service;

public interface MagicMarkupService  {
	String transform(String html);
	String transform4Pdf(String html, String localHostname);
}
