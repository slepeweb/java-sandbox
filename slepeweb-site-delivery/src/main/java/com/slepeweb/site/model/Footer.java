package com.slepeweb.site.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Footer implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<LinkTarget> links;

	public Footer() {
		this.links = new ArrayList<LinkTarget>();
	}
	
	public List<LinkTarget> getLinks() {
		return links;
	}
}
