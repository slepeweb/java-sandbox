package com.slepeweb.site.model;

import com.slepeweb.cms.bean.Link;

public class TabbedComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;

	public TabbedComponent setup(Link l) {
		super.setup(l);
		return this;
	}
	
	public String toString() {
		return String.format("TabbedComponent (%s): %s", getType(), getHeading());
	}
	
}
