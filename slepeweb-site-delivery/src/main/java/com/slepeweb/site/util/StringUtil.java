package com.slepeweb.site.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
	
	public static String toIdentifier(String s) {
		return StringUtils.replaceChars(s.toLowerCase(), "- ", "__");
	}
	
	public static String[][] splitLinesIntoParts(String s, String delimRegex, Integer minPartsPerLine) {
		List<String[]> list = new ArrayList<String[]>();
		String[] parts;
		
		for (String line : s.split("[\\n\\r]")) {
			parts = splitLineIntoParts(line, delimRegex, minPartsPerLine);
			if (parts != null) {
				list.add(parts);
			}
		}
		
		String[][] result = new String[list.size()][];
		return list.toArray(result);
	}
	
	public static String[] splitLineIntoParts(String s, String delimRegex, Integer minPartsPerLine) {
		String[] arr = s.split(delimRegex);
				
		if (minPartsPerLine == null || arr.length >= minPartsPerLine) {
			// Trim parts
			for (int i = 0; i < arr.length; i++) {
				arr[i] = arr[i].trim();
			}
			
			return arr;
		}
		
		return null;
	}

}
