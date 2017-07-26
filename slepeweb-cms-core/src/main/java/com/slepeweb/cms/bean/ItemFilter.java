package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;

public class ItemFilter {

	private String[] types, linkNames, simpleNamePatterns;
	
	public boolean test(Item i) {
		boolean itemTypeMatch, simplenameMatch;
		itemTypeMatch = simplenameMatch = true;

		if (getTypes() != null) {
			itemTypeMatch = matches(getTypes(), i.getType().getName());
		}

		if (getSimpleNamePatterns() != null) {
			simplenameMatch = matchesRegex(getSimpleNamePatterns(), i.getSimpleName());
		}

		return itemTypeMatch && simplenameMatch;
	}

	public boolean test(Link l) {
		boolean linkNameMatch = true;

		if (getLinkNames() != null) {
			linkNameMatch = matches(getLinkNames(), l.getName());
		}

		return linkNameMatch && test(l.getChild());
	}
	
	public List<Item> filterItems(List<Item> list) {
		List<Item> result = new ArrayList<Item>(list.size());
		for (Item i : list) {
			if (test(i)) {
				result.add(i);
			}
		}
		return result;
	}

	public List<Item> filterLinks(List<Link> list) {
		List<Item> result = new ArrayList<Item>(list.size());
		for (Link l : list) {
			if (test(l)) {
				result.add(l.getChild());
			}
		}
		return result;
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
	
	private <T> boolean matchesRegex(String[] arr, String target) {
		// Must match ANY element in the array
		for (String pattern : arr) {
			if (target.matches(pattern)) {
				return true;
			}
		}
		
		// There were NO matching elements in the array
		return false;
	}
	
	public String[] getTypes() {
		return types;
	}

	public ItemFilter setTypes(String[] types) {
		this.types = types;
		return this;
	}

	public String[] getLinkNames() {
		return linkNames;
	}

	public ItemFilter setLinkNames(String[] linkNames) {
		this.linkNames = linkNames;
		return this;
	}

	public String[] getSimpleNamePatterns() {
		return simpleNamePatterns;
	}

	public ItemFilter setSimpleNamePatterns(String[] simpleNames) {
		this.simpleNamePatterns = simpleNames;
		return this;
	}
	
}
