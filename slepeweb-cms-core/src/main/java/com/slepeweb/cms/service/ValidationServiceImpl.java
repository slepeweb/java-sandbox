package com.slepeweb.cms.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.guidance.IValidator;

@Service
public class ValidationServiceImpl implements ValidationService {
	private static Logger LOG = Logger.getLogger(ValidationServiceImpl.class);
	
	private Map<String, IValidator> map = new HashMap<String, IValidator>();
	
	public IValidator get(String classPath) {
		if (StringUtils.isNotBlank(classPath)) {
			IValidator iv = this.map.get(classPath);
			if (iv == null) {
				iv = instantiate(classPath);
				if (iv != null) {
					this.map.put(classPath, iv);
					LOG.info(String.format("Registered validator [%s]",  classPath));
				}
			}
		
			return iv;
		}
		
		return null;
	}
	
	private IValidator instantiate(String classPath) {
		try {
			Class<?> clazz = Class.forName(classPath);
			return (IValidator) clazz.getDeclaredConstructor().newInstance();
		}
		catch (Exception e) {
			LOG.error("Failed to identify validator instance", e);
			return null;
		}
	}
	
}
