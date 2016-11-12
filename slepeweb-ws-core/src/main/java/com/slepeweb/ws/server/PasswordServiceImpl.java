package com.slepeweb.ws.server;

import javax.jws.WebService;

import com.slepeweb.password.PasswordGenerator;
import com.slepeweb.ws.bean.PasswordBean;

@WebService(endpointInterface = "com.slepeweb.ws.server.PasswordService")
public class PasswordServiceImpl implements PasswordService {
	
	public PasswordBean getPassword(String source, String key) {
		PasswordBean pwd = new PasswordBean();
		PasswordGenerator generator = new PasswordGenerator();
		pwd.setSeed(source);		
		pwd.setPassword(generator.encode(source, key));
		return pwd;
	}
}
