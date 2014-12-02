package com.slepeweb.site.model;

import com.slepeweb.cms.bean.Link;

public class ImageComponent extends SimpleComponent {

	private static final long serialVersionUID = 1L;
	private String src;

	public ImageComponent setup(Link l) {
		super.setup(l);
		setHeading(l.getChild().getFieldValue("alt"));
		setBlurb(l.getChild().getFieldValue("caption"));
		return this;
	}

	public String toString() {
		return String.format("ImageComponent (%s): %s", getType(), getHeading());
	}
	
	public String getAlt() {
		return getHeading();
	}
	
	public String getCaption() {
		return getBlurb();
	}

	public String getSrc() {
		return src;
	}

	public ImageComponent setSrc(String src) {
		this.src = src;
		return this;
	}
}
