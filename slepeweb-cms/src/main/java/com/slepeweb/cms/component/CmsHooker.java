package com.slepeweb.cms.component;

import java.util.HashMap;
import java.util.Map;

public class CmsHooker {
	private Map<String, ICmsHook> hooks = new HashMap<String, ICmsHook>();
	
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public CmsHooker(Map<String, String> classes) {
		String className;
		Class c;
		ICmsHook h;
		
		for (String siteName : classes.keySet()) {
			className = classes.get(siteName);
			try {
				c = Class.forName(className);
				h = (ICmsHook) c.newInstance();
				this.hooks.put(siteName, h);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public ICmsHook getHook(String siteName) {
		return this.hooks.get(siteName);
	}
}
