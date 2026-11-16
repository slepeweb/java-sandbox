package com.slepeweb.cms.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.Shortcut;
import com.slepeweb.cms.constant.ItemTypeName;

public class CmsUtil {
	
	public static final String MINIPATH_PATTERN_STR = "^(/(\\w\\w))?/\\$_(\\d+)$";
	public static final Pattern MINIPATH_PATTERN = Pattern.compile(MINIPATH_PATTERN_STR);

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
	
	public static boolean isAppropriate4Navigation(Item i) {
		return
				! i.getType().isMedia() &&
				! i.isHiddenFromNav() &&
				! i.getType().getName().equals(ItemTypeName.CONTENT_FOLDER) &&
				i.isAccessible();
	}
	
	public static Item transformShortcut4Delivery(Item i) {
		
		if (! (i.isShortcut() && i.getCmsService().isDeliveryContext())) {
			return i;
		}
		
		List<Link> links = i.getCmsService().getLinkService().getLinks(i.getId(), LinkType.shortcut);
		
		if (links.size() > 0) {
			Item target = links.get(0).getChild();
			
			Shortcut sh = (Shortcut) i;
			String shortcutPath = i.getPath();
			sh.setId4OrthogonalParent(i.getId());				
			
			// Merge MOST data from target to shortcut
			sh.assimilate(target);
			
			// For the purpose of item linkages, assign the id of the target item to the shortcut
			sh.setId(target.getId());
			sh.setReferred(target);
			
			// But retain the shortcut path
			sh.setPath(shortcutPath);
			
			return sh;
		}

		return i;
	}
}
