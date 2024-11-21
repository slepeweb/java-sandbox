package com.slepeweb.site.model;

import com.slepeweb.cms.bean.Link;

public class CustomComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;

	public CustomComponent setup(Link l) {
		super.setup(l);
		return this;
	}
}
