package com.slepeweb.site.model;

import com.slepeweb.cms.bean.Link;

public class TwoBlockComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	private String left, right;

	public TwoBlockComponent setup(Link l) {
		super.setup(l);
		setAltViews(true);
		
		this.left = l.getChild().getFieldValue("left");
		this.right = l.getChild().getFieldValue("right");
		return this;
	}
	
	public String getLeft() {
		return left;
	}

	public String getRight() {
		return right;
	}
}
