package com.slepeweb.site.model;

import com.slepeweb.cms.bean.Link;

public class CustomComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	//private static Logger LOG = Logger.getLogger(CustomComponent.class);

	private String jsp;

	public CustomComponent setup(Link l) {
		super.setup(l);
		this.jsp = l.getChild().getFieldValue("data");		
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("CustomComponent (%s): %s", getType(), getHeading());
	}

	public String getJsp() {
		return jsp;
	}

}
