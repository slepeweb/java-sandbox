package com.slepeweb.site.model;

import com.slepeweb.cms.bean.Link;

public class StyleComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	private String css;

	public StyleComponent setup(Link l) {
		super.setup(l);
		setAltViews(true);
		
		this.css = l.getChild().getFieldValue("style");
		return this;
	}
	
	public String getCss() {
		return css;
	}

}
