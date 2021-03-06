package com.slepeweb.site.model;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Link;

public class ImageComponent extends SimpleComponent {

	private static final long serialVersionUID = 1L;
	private String src;
	private Integer maxWidth;

	public ImageComponent setup(Link l) {
		super.setup(l);
		setHeading(l.getChild().getFieldValue("alt"));
		setBlurb(l.getChild().getFieldValue("caption"));
		
		String max = l.getChild().getFieldValue("maxwidth");
		if (StringUtils.isNumeric(max)) {
			setMaxWidth(Integer.valueOf(max));
		}
		
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

	public Integer getMaxWidth() {
		return maxWidth;
	}

	public ImageComponent setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}
}
