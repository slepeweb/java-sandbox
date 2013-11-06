package com.slepeweb.sandbox.ws.soap;

import javax.jws.WebService;

import org.apache.commons.lang3.StringUtils;

@WebService(endpointInterface = "com.slepeweb.sandbox.ws.soap.PasswordManager")
public class PasswordManagerImpl implements PasswordManager {

	public PasswordBean getPassword(String organisation) {
		
		PasswordBean pwd = new PasswordBean();
		int offset = organisation.length();
		int maxLength = 8;
		String seed;
		
		if (organisation.length() < maxLength) {
			seed = StringUtils.overlay("organize", organisation, 0, organisation.length());
		}
		else {
			seed = organisation.substring(0, maxLength);
		}
		
		pwd.setSeed(seed);
		
		char[] chars = seed.toCharArray();
		
		for (int i=0; i<seed.length(); i++) {
			chars[i] += offset;
		}
		
		pwd.setPassword(String.valueOf(chars));
		return pwd;
	}

}
