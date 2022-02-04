package com.slepeweb.common.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
	
	public static String wrapWithMarkup(String html, String tag, String clazz) {
		
		if (StringUtils.isBlank(html)) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		String classSetting = StringUtils.isBlank(clazz) ? "" : String.format(" class=\"%s\"", clazz);
		String openTag = String.format("<%s%s>", tag, classSetting);
		String closeTag = String.format("</%s>", tag);
		String para;
		
		for (String part : html.trim().split("(\\n\\s*){2,}")) {
			para = part.trim();
			if (para.length() > 0) {
				if (! para.substring(0, 1).equals("<")) {
					sb.append(openTag).append(para).append(closeTag).toString();
				}
				else {
					sb.append(para);
				}
			}
		}
		
		return sb.toString();
	}
	
	public static String compress(String s) {
		if (s != null) {
			return s.toLowerCase().replaceAll("[\'\" ]", "");
		}
		return null;
	}
	
	public static boolean isPositive(String str) {
		if (str != null) {
			String s = str.trim();
			return 
					s.equalsIgnoreCase("yes") ||
					s.equalsIgnoreCase("true") ||
					s.equalsIgnoreCase("on") ||
					s.equalsIgnoreCase("1");
		}
		
		return false;
	}
	
	public static Object tertiaryOp(boolean test, Object trueResult, Object falseResult) {
		return test ? trueResult : falseResult;
	}
}
