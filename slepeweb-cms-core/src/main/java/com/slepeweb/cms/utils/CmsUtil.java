package com.slepeweb.cms.utils;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;

public class CmsUtil {
	
	public static String getParentPathFromPath(Item i) {
		if (! i.isRoot()) {
			return getParentPathFromPath(i.getPath());
		}
		
		// A null parent means that this item is a root item
		return null;
	}

	public static String getParentPathFromPath(String path) {
		if (StringUtils.isNotBlank(path)) {
			int c = path.lastIndexOf("/");
			if (c > 0) {
				return path.substring(0, c);
			}
			return "/";
		}
		
		// A null parent means that this item is a root item
		return null;
	}

	public static String getSimplename(String path) {
		if (StringUtils.isNotBlank(path)) {
			int c = path.lastIndexOf("/");
			if (c > 0) {
				return path.substring(c + 1);
			}
			return "";
		}
		
		// A null parent means that this item is a root item
		return null;
	}
}
