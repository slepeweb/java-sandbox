package com.slepeweb.site.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
	
	public static String toIdentifier(String s) {
		return StringUtils.replace(s.toLowerCase(), "- ", "__");
	}
}
