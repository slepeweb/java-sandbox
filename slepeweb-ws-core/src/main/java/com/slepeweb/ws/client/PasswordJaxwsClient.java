package com.slepeweb.ws.client;

import com.slepeweb.ws.bean.PasswordBean;

public interface PasswordJaxwsClient {
	PasswordBean getPassword(String source, String key);
}
