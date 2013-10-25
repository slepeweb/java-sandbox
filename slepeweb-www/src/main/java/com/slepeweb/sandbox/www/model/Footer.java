package com.slepeweb.sandbox.www.model;

import java.util.ArrayList;
import java.util.List;

public class Footer {
	private List<Link> links;

	public Footer() {
		this.links = new ArrayList<Link>();
	}
	
	public List<Link> getLinks() {
		return links;
	}
}
