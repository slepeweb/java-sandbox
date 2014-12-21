package com.slepeweb.ws.server;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.slepeweb.ws.bean.PasswordBean;

@WebService
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL)
public interface PasswordService {
	@WebMethod PasswordBean getPassword(String org);
}
