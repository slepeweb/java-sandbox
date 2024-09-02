package com.slepeweb.site.model;

import com.slepeweb.cms.bean.Link;

public class AccordionComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;

	public AccordionComponent setup(Link l) {
		super.setup(l);
		return this;
	}
	
	public String toString() {
		return String.format("AccordionComponent (%s): %s", getType(), getHeading());
	}
	
}
