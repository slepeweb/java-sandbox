package com.slepeweb.cms.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.constant.ItemTypeName;

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

	public static List<Item> toItems(List<Link> list) {
		List<Item> result = new ArrayList<Item>(list.size());
		for (Link l : list) {
			result.add(l.getChild());
		}
		return result;
	}
	
	public static String getFieldValue(Item i, String variable, boolean resolve, String dflt) {
		return getFieldValue(i, variable, i.getSite().getLanguage(), resolve, dflt);
	}
	
	public static String getFieldValue(Item i, String variable, String language, boolean resolve, String dflt) {
		FieldValue fv = i.getFieldValueObj(variable, language);
		String result = dflt;
		
		if (fv != null) {
			result = resolve ? fv.getStringValueResolved() : fv.getStringValue();
		}
		
		return result;
	}
	
	public static boolean isApt4Navigation(Item i) {
		return
				! i.getType().isMedia() &&
				! i.isHiddenFromNav() &&
				! i.getType().getName().equals(ItemTypeName.CONTENT_FOLDER) &&
				i.isAccessible();
	}
}
