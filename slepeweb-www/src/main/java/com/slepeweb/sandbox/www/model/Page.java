package com.slepeweb.sandbox.www.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.slepeweb.sandbox.www.model.User.Role;

public class Page {
	private String path;
	private Header header;
	private Footer footer;
	private Sidebar leftSidebar, rightSidebar;
	private String title, heading, body, view;
	private List<Component> components;
	private List<Role> roles;
	
	public Page() {
		this.header = new Header();
		this.footer = new Footer();
		this.leftSidebar = new Sidebar();
		this.rightSidebar = new Sidebar();
		this.components = new ArrayList<Component>();
	}
	
	public Page addRole(Role r) {
		if (getRoles() == null) {
			setRoles(new ArrayList<Role>());
		}
		getRoles().add(r);
		return this;
	}
	
	public boolean isAccessibleBy(User u) {
		if (getRoles() == null) {
			return true;
		}
		else {
			if (u != null && u.getRoles() != null) {
				return ! Collections.disjoint(getRoles(), u.getRoles());
			}
		}
		return false;
	}
	
	public Header getHeader() {
		return header;
	}
	
	public Page setHeader(Header header) {
		this.header = header;
		return this;
	}
	
	public Footer getFooter() {
		return footer;
	}
	
	public Page setFooter(Footer footer) {
		this.footer = footer;
		return this;
	}
	
	public Sidebar getLeftSidebar() {
		return leftSidebar;
	}
	
	public Page setLeftSidebar(Sidebar leftSidebar) {
		this.leftSidebar = leftSidebar;
		return this;
	}
	
	public Sidebar getRightSidebar() {
		return rightSidebar;
	}
	
	public Page setRightSidebar(Sidebar rightSidebar) {
		this.rightSidebar = rightSidebar;
		return this;
	}
	
	public String getHeading() {
		return heading;
	}
	
	public Page setHeading(String heading) {
		this.heading = heading;
		return this;
	}
	
	public String getBody() {
		return body;
	}
	
	public Page setBody(String body) {
		this.body = body;
		return this;
	}
	
	public String getView() {
		return view;
	}
	
	public Page setView(String view) {
		this.view = view;
		return this;
	}
	
	public List<Component> getComponents() {
		return components;
	}

	public String getPath() {
		return path;
	}

	public Page setPath(String path) {
		this.path = path;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Page setTitle(String title) {
		this.title = title;
		return this;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
