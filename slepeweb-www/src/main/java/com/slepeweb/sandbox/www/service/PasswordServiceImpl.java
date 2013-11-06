package com.slepeweb.sandbox.www.service;

import java.net.URL;

import javax.xml.namespace.QName;

import org.springframework.stereotype.Service;

import com.slepeweb.sandbox.ws.soap.PasswordBean;
import com.slepeweb.sandbox.ws.soap.PasswordManager;

@Service("passwordService")
public class PasswordServiceImpl implements PasswordService {

	public PasswordBean getPassword(String seed) {

		try {
			URL url = new URL("http://localhost:8080/jaxws/password?wsdl");
			QName qname = new QName("http://soap.ws.sandbox.slepeweb.com/", "PasswordManagerImplService");
	
			javax.xml.ws.Service service = javax.xml.ws.Service.create(url, qname);
	
			PasswordManager pm = service.getPort(PasswordManager.class);
			return pm.getPassword(seed);
		}
		catch (Exception e) {
			// TODO: add logging
		}
		
		return null;
	}
}
