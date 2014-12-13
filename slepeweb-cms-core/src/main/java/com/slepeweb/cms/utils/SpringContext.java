package com.slepeweb.cms.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContext implements ApplicationContextAware {

	private static ApplicationContext CTX;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		CTX = context;
	}

	public static ApplicationContext getApplicationContext() {
		return CTX;
	}
}
