package com.slepeweb.sandbox.www.service;

import com.slepeweb.sandbox.ws.soap.PasswordBean;


public interface PasswordService {
	PasswordBean getPassword(String org);
}
