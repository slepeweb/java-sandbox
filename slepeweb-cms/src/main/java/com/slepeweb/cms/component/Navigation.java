package com.slepeweb.cms.component;

import java.util.ArrayList;
import java.util.List;

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
		private String title, key;
		private boolean folder, lazy;
		private List<Node> children = new ArrayList<Node>();
		
		public Node addChild(Node n) {
			getChildren().add(n);
			return this;
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
			return getChildren().size() > 0 || this.folder;
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
	}

}
