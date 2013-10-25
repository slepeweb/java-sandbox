package com.slepeweb.sandbox.acm.mvc;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer;

/**
 * Allow us access to properties outside of Spring (E.g. in supporting servlets
 * such as MediasurfaceServlet).
 * 
 * @author adam
 * 
 */
@SuppressWarnings("deprecation")
public class ApplicationProperties extends ServletContextPropertyPlaceholderConfigurer {
	private static Map<String, String> propertiesMap;
	private ServletContext servletContext;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
			throws BeansException {
		super.processProperties(beanFactory, props);

		propertiesMap = new HashMap<String, String>();
		
		processServletContextProperties();
		
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			propertiesMap.put(keyStr, parseStringValue(props.getProperty(keyStr), props, new HashSet()));
		}
		
	}

	public static Map<String,String> getProperties() {
		return Collections.unmodifiableMap(propertiesMap);
	}
	
	@Override
	public void setServletContext(ServletContext ctx) {
		super.setServletContext(ctx);
		this.servletContext = ctx;
	}
	
	private void processServletContextProperties() {
		@SuppressWarnings("rawtypes")
		Enumeration it = this.servletContext.getInitParameterNames();
		if(it!=null) {
			while(it.hasMoreElements()) {
				String name = (String)it.nextElement();
				propertiesMap.put(name, this.servletContext.getInitParameter(name));
			}
		}
	}
}
