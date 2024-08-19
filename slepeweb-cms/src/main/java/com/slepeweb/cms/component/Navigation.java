package com.slepeweb.cms.component;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Shortcut;

public class Navigation {
	
	/*
	 * See example-leftnav.json for example output.
	 */
	
	private List<Node> nodes = new ArrayList<Node>();
	
	public void addNode(Node n) {
		getNodes().add(n);
	}
	
	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	/* This class represents a FancyTree node - see FancyTree documentation */
	public static class Node {
		public static final String ICON_PREFIX = "cms-icon-";
		
		private Node parentNode;
		private String title, key, extraClasses;
		private boolean folder, lazy = true, expanded, selected, shortcut, accessible;
		private List<Node> children;
		
		public static Node toNode(Item i) {
			Node n = new Navigation.Node().
					setTitle(i.getName()).
					setKey(i.getOrigId().toString()).
					setAccessible(i.isAccessible()).
					setShortcut(i.isShortcut()).
					setExtraClasses(i);
			
			return n;
		}
		
		@Override
		public String toString() {
			return getTitle();
		}
		
		public Node addChild(Node n) {
			if (this.children == null) {
				this.children = new ArrayList<Navigation.Node>();
			}
			
			this.children.add(n);
			n.setParentNode(this);
			n.getParentNode().setFolder(true).setExpanded(true);
			return this;
		}
		
		public void addChildren(List<Navigation.Node> children) {
			for (Navigation.Node n : children) {
				addChild(n);
			}
		}
		
		public String getTitle() {
			return title;
		}
		
		public Node setTitle(String title) {
			this.title = title;
			return this;
		}
		
		public String getKey() {
			return key;
		}
		
		public Node setKey(String key) {
			this.key = key;
			return this;
		}
		
		public boolean isFolder() {
			return this.folder;
		}
		
		public Node setFolder(boolean folder) {
			this.folder = folder;
			return this;
		}
		
		public List<Node> getChildren() {
			return children;
		}
		
		public Node setChildren(List<Node> children) {
			this.children = children;
			return this;
		}

		public boolean isLazy() {
			return lazy;
		}

		public Node setLazy(boolean lazy) {
			this.lazy = lazy;
			return this;
		}

		@JsonIgnore public Node getParentNode() {
			return parentNode;
		}

		public Node setParentNode(Node parent) {
			this.parentNode = parent;
			return this;
		}

		public boolean isExpanded() {
			return expanded;
		}

		public Node setExpanded(boolean expanded) {
			this.expanded = expanded;
			return this;
		}

		public boolean isSelected() {
			return selected;
		}

		public Node setSelected(boolean selected) {
			this.selected = selected;
			return this;
		}

		public boolean isShortcut() {
			return shortcut;
		}

		public Node setShortcut(boolean shortcut) {
			this.shortcut = shortcut;
			return this;
		}

		public boolean isAccessible() {
			return accessible;
		}

		public Node setAccessible(boolean accessible) {
			this.accessible = accessible;
			return this;
		}

		public Node setExtraClasses(Item i) {
			ItemType t = i.getType(); 
			
			if (i.isShortcut()) {
				Shortcut sh = (Shortcut) i;
				if (sh.getReferred() != null) {
					t = sh.getReferred().getType();
				}
			}
			
			String typeName = t.getName().toLowerCase();
			
			if (typeName.endsWith("homepage")) {
				typeName = "homepage";
			}
			else if (typeName.startsWith("image")) {
				typeName = "image";
			}
			
			this.extraClasses = composeExtraClasses(typeName);
			return this;
		}
		
		public Node setExtraClasses(String typeName) {			
			this.extraClasses = composeExtraClasses(typeName);			
			return this;
		}

		public String getExtraClasses() {
			return extraClasses;
		}

		public String composeExtraClasses(String iconType) {
			String clazz = ICON_PREFIX + iconType;
			clazz += isShortcut() ? "-shortcut" : "";
			clazz += ! isAccessible() ? " inaccessible" : "";
			return clazz;
		}
	}

}
