package com.slepeweb.ws.server;

import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;

import com.slepeweb.ws.bean.PasswordBean;

@WebService(endpointInterface = "com.slepeweb.ws.server.PasswordService")
public class PasswordServiceImpl implements PasswordService {
	private static Map<Character, Character> MAP = new HashMap<Character, Character>(11);
	
	static {
		MAP.put('i', '1');
		MAP.put('a', '@');
		MAP.put('l', '!');
		MAP.put('o', '0');
		MAP.put('s', '5');
		MAP.put('b', '8');
		MAP.put('z', '2');
	}
	
	public PasswordBean getPassword(String organisation) {
		PasswordBean pwd = new PasswordBean();
		int offset = 1;
				
		pwd.setSeed(organisation.toLowerCase());		
		char[] chars = pwd.getSeed().toCharArray();
		
		for (int i=0; i< pwd.getSeed().length(); i++) {
			if (chars[i] >= 65 && chars[i] <= 122) {
				chars[i] += offset;
				if (chars[i] > 122) {
					chars[i] = (char) (65 + chars[i] - 123);
				}
			}
			else {
				chars[i]='$';
			}
		}
		
		Character mapped;
		for (int i=0; i< chars.length; i++) {
			mapped = MAP.get(chars[i]);
			if (mapped != null) {
				chars[i] = mapped;
			}
		}
		
		pwd.setPassword(String.valueOf(chars));
		return pwd;
	}
}
