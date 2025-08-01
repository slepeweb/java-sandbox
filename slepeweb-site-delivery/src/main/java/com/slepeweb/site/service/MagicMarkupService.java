package com.slepeweb.site.service;

import com.slepeweb.cms.component.Passkey;

public interface MagicMarkupService  {
	String transform(String html);
	String transform4Pdf(String html, String localHostname, Passkey passkey);
}
