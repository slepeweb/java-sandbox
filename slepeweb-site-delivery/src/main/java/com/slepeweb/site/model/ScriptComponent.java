package com.slepeweb.site.model;

import com.slepeweb.cms.bean.Link;

public class ScriptComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	private String js;

	public ScriptComponent setup(Link l) {
		super.setup(l);
		setAltViews(true);
		
		this.js = l.getChild().getFieldValue("script");
		return this;
	}
	
	public String getJs() {
		return js;
	}

}
