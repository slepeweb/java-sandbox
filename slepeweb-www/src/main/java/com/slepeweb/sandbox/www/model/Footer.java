package com.slepeweb.sandbox.www.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Footer implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Link> links;

	public Footer() {
		this.links = new ArrayList<Link>();
	}
	
	public List<Link> getLinks() {
		return links;
	}
}
