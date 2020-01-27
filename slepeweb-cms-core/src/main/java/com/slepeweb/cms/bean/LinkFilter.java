package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;

public class LinkFilter {

	private String[] linkTypes, names, itemTypes;
	
	private boolean test(Link l) {
		boolean linkTypeMatch, nameMatch, itemTypeMatch;
		linkTypeMatch = nameMatch = itemTypeMatch = true;

		if (getLinkTypes() != null) {
			linkTypeMatch = matches(getLinkTypes(), l.getType());
		}

		if (getNames() != null) {
			nameMatch = matches(getNames(), l.getName());
		}
		
		if (getItemTypes() != null) {
			itemTypeMatch = matches(getItemTypes(), l.getChild().getType().getName());
		}

		return linkTypeMatch && nameMatch && itemTypeMatch;
	}

	public List<Link> filterLinks(List<Link> list) {
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

	public String[] getNames() {
		return names;
	}

	public LinkFilter setName(String linkName) {
		this.names = new String[] {linkName};
		return this;
	}

	public LinkFilter setNames(String[] linkNames) {
		this.names = linkNames;
		return this;
	}

	public String[] getItemTypes() {
		return itemTypes;
	}

	public void setItemTypes(String[] itemTypes) {
		this.itemTypes = itemTypes;
	}

	public LinkFilter setItemType(String typeName) {
		this.itemTypes = new String[] {typeName};
		return this;
	}

}
