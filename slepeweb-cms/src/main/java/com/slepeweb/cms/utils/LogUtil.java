package com.slepeweb.cms.utils;

import org.apache.log4j.Logger;

public class LogUtil {
	public static String info(Logger logger, String template, String arg) {
		String s = String.format(template + " [%s]", arg);
		logger.info(s);
		return s;
	}
	
	public static String warn(Logger logger, String template, String arg) {
		String s = String.format(template + " [%s]", arg);
		logger.warn(s);
		return s;
	}
	
	public static String error(Logger logger, String template, String arg, Exception e) {
		String s = String.format(template + " [%s]", arg);
		logger.error(s, e);
		return s;
	}
}
