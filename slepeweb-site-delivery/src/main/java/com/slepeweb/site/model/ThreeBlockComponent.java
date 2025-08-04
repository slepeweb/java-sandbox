package com.slepeweb.site.model;

import com.slepeweb.cms.bean.Link;

public class ThreeBlockComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	private String left, right, middle;

	public ThreeBlockComponent setup(Link l) {
		super.setup(l);
		setAltViews(true);
		
		this.left = l.getChild().getFieldValue("left");
		this.middle = l.getChild().getFieldValue("middle");
		this.right = l.getChild().getFieldValue("right");
		return this;
	}
	
	public String getLeft() {
		return left;
	}

	public String getMiddle() {
		return middle;
	}

	public String getRight() {
		return right;
	}
}
