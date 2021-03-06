package com.slepeweb.cms.utils;

import org.apache.log4j.Logger;

public class LogUtil {
	public static String debug(Logger logger, String template, Object ... args) {
		String s = compose(template, args);
		logger.debug(s);
		return s;
	}
	
	public static String info(Logger logger, String template, Object ... args) {
		String s = compose(template, args);
		logger.info(s);
		return s;
	}
	
	public static String warn(Logger logger, String template, Object ... args) {
		String s = compose(template, args);
		logger.warn(s);
		return s;
	}
	
	public static String error(Logger logger, String template, Exception e, Object ... args) {
		String s = compose(template, args);
		logger.error(s, e);
		return s;
	}
	
	public static String compose(String template, Object ... args) {
		StringBuilder sb = new StringBuilder(template).append(" [");
		for (int i = 0; i < args.length; i++) {
			if (i > 0 && i < args.length) {
				sb.append(" / ");
			}
			sb.append("%s");
		}
		sb.append("]");
		return String.format(sb.toString(), args);
	}
}
