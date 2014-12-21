package com.slepeweb.ws.client;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.ws.bean.PasswordBean;
import com.slepeweb.ws.server.PasswordService;

@Service("passwordJaxwsClient")
public class PasswordJaxwsClientImpl implements PasswordJaxwsClient {
	private static Logger LOG = Logger.getLogger(PasswordJaxwsClientImpl.class);
		
	@Autowired private PasswordService passwordJaxwsProxy;	

	public PasswordBean getPassword(String seed) {
		LOG.info(String.format("Getting password at %1$tH:%1$tM:%1$tS", System.currentTimeMillis()));
		
		try {
			PasswordBean bean = this.passwordJaxwsProxy.getPassword(seed);
			return bean;
		}
		catch (Exception e) {
			LOG.error("Failed to get password", e);
			PasswordBean error = new PasswordBean();
			error.setError(e.getMessage());
			return error;
		}		
	}

}
