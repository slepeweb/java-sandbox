package com.slepeweb.cms.utils;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

public class CacheKeyGenerator implements KeyGenerator {
	
	public Object generate(Object target, Method method, Object... params) {
		
		StringBuilder sb = new StringBuilder(method.getName());
		
		for (Object o : params) {
			sb.append("-").append(o == null ? "null" : o.toString());
		}
		
		return sb.toString();
	}
}
