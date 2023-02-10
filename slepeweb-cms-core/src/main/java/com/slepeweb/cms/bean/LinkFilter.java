package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.cms.utils.CmsUtil;

public class LinkFilter {

	private String[] linkTypes, linkNames, itemTypes, mimeTypePatterns,
		/* Only used currently for identifying images in commerce app */ simpleNamePatterns;
	
	private boolean test(Link l) {
		boolean linkTypeMatch = true, linkNameMatch = true, itemTypeMatch = true, 
				simplenameMatch = true, mimeTypeMatch = true;

		if (getLinkTypes() != null) {
			linkTypeMatch = matches(getLinkTypes(), l.getType());
		}

		if (getLinkNames() != null) {
			linkNameMatch = matches(getLinkNames(), l.getName());
		}
		
		if (getItemTypes() != null) {
			itemTypeMatch = matches(getItemTypes(), l.getChild().getType().getName());
		}

		if (getSimpleNamePatterns() != null) {
			simplenameMatch = matchesRegex(getSimpleNamePatterns(), l.getChild().getSimpleName());
		}

		if (getMimeTypePatterns() != null) {
			mimeTypeMatch = matchesRegex(getMimeTypePatterns(), l.getChild().getType().getMimeType());
		}

		return linkTypeMatch && linkNameMatch && itemTypeMatch && simplenameMatch && mimeTypeMatch;
	}

	public List<Link> filter(List<Link> list) {
		List<Link> result = new ArrayList<Link>(list.size());
		for (Link l : list) {
			if (test(l)) {
				result.add(l);
			}
		}
		return result;
	}

	public Link filterFirst(List<Link> list) {
		for (Link l : list) {
			if (test(l)) {
				return l;
			}
		}
		return null;
	}
	
	public List<Item> filterItems(List<Link> list) {
		return CmsUtil.toItems(filter(list));
	}

	public Item filterFirstItem(List<Link> list) {
		Link l = filterFirst(list);
		if (l != null) {
			return l.getChild();
		}
		return null;
	}
	
	private <T> boolean matches(T[] arr, T target) {
		// Must match ANY element in the array
		for (T ele : arr) {
			if (ele.equals(target)) {
				return true;
			}
		}
		
		// There were NO matching elements in the array
		return false;
	}
	
	private <T> boolean matchesRegex(String[] regex, String target) {
		// Must match ANY element in the array
		for (String pattern : regex) {
			if (target.matches(pattern)) {
				return true;
			}
		}
		
		// There were NO matching elements in the array
		return false;
	}
	public String[] getLinkTypes() {
		return linkTypes;
	}

	public LinkFilter setLinkType(String typeName) {
		this.linkTypes = new String[] {typeName};
		return this;
	}

	public LinkFilter setLinkTypes(String[] types) {
		this.linkTypes = types;
		return this;
	}

	public String[] getLinkNames() {
		return linkNames;
	}

	public LinkFilter setLinkName(String linkName) {
		this.linkNames = new String[] {linkName};
		return this;
	}

	public LinkFilter setLinkNames(String[] linkNames) {
		this.linkNames = linkNames;
		return this;
	}
		
	public String[] getItemTypes() {
		return itemTypes;
	}

	public LinkFilter setItemType(String typeName) {
		this.itemTypes = new String[] {typeName};
		return this;
	}

	public LinkFilter setItemTypes(String[] types) {
		this.itemTypes = types;
		return this;
	}

	public String[] getSimpleNamePatterns() {
		return simpleNamePatterns;
	}

	public LinkFilter setSimpleNamePattern(String simpleName) {
		this.simpleNamePatterns = new String[] {simpleName};
		return this;
	}

	public LinkFilter setSimpleNamePatterns(String[] simpleNames) {
		this.simpleNamePatterns = simpleNames;
		return this;
	}

	public String[] getMimeTypePatterns() {
		return mimeTypePatterns;
	}

	public LinkFilter setMimeTypePatterns(String[] pattern) {
		this.mimeTypePatterns = pattern;
		return this;
	}
	
	public LinkFilter setMimeTypePatterns(String pattern) {
		this.mimeTypePatterns = new String[] {pattern};
		return this;
	}
	
}
