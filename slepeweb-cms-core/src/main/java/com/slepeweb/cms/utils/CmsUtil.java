package com.slepeweb.cms.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.Media;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

	public static String forward2MediaStreamer(Item item, HttpServletRequest req, HttpServletResponse res) {
		
		String msg = null;
		
		String viewParam = req.getParameter("view");
		boolean thumbnailRequired = false;
		if (StringUtils.isNotBlank(viewParam)) {
			thumbnailRequired = viewParam.equals("thumbnail");
		}

		Media m = thumbnailRequired ? item.getThumbnail() : item.getMedia();
		
		if (m != null && m.isBinaryContentLoaded()) {
			req.setAttribute("_media", m);
			String mediaType = item.getType().isVideo() ? "video" : "image";
			String servletPath = "/stream/" + mediaType;
			
			try {
				req.getRequestDispatcher(servletPath).forward(req, res);
			}
			catch (Exception e) {
				msg = e.getMessage();
			}
		}
		else {
			msg = String.format("No media found for item %s", item.getPath());
		}
		
		return msg;
	}
}
