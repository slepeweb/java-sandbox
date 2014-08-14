package com.slepeweb.cms.component;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Navigation {
	
//	String json = "[" +
//			"{'title': 'Node 1', 'key': '1'}, " +
//			"{'title': 'Folder 2', 'key': '2', 'folder': true, 'children': [ " +
//				"{'title': 'Node 2.1', 'key': '3'}," +
//				"{'title': 'Node 2.2', 'key': '4'}]}]";
	
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
	
	public static class Node {
		private Node parentNode;
		private String title, key;
		private boolean folder, lazy = true, expanded, selected;
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

		public void setParentNode(Node parent) {
			this.parentNode = parent;
		}

		public boolean isExpanded() {
			return expanded;
		}

		public void setExpanded(boolean expanded) {
			this.expanded = expanded;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}		
	}

}
