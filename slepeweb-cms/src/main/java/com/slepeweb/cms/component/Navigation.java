package com.slepeweb.cms.component;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

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
		private Node parentNode;
		private String title, key, extraClasses;
		private boolean folder, lazy = true, expanded, selected, shortcut;
		private List<Node> children = new ArrayList<Node>();
		
		public Node addChild(Node n) {
			getChildren().add(n);
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

		/*
		 * If this node represents a shortcut, then modify the key, so that
		 * it doesn't conflict with the key of the 'real' node.
		 */
		public Node setShortcut(boolean shortcut) {
			this.shortcut = shortcut;
			if (shortcut) {
				this.key += ".s";
				if (this.extraClasses == null) {
					setExtraClasses("shortcut");
				}
				else {
					setExtraClasses(getExtraClasses() + " shortcut");
				}
			}
			return this;
		}

		public String getExtraClasses() {
			return this.extraClasses;
		}

		public Node setExtraClasses(String extraClasses) {
			this.extraClasses = extraClasses;
			return this;
		}
	}

}
