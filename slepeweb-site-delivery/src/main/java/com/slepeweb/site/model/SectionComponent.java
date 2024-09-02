package com.slepeweb.site.model;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.cms.bean.Link;

public class SectionComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	private Image image;
	private List<LinkTarget> targets;
	private String identifier;

	public SectionComponent setup(Link l) {
		super.setup(l);
		this.targets = new ArrayList<LinkTarget>();
		this.identifier = l.getChild().getFieldValue("identifier");
		return this;
	}
	
	public String toString() {
		return String.format("SectionComponent (%s): %s", getType(), getHeading());
	}
	
	public Image getImage() {
		return image;
	}

	public SectionComponent setImage(Image media) {
		this.image = media;
		return this;
	}

	public List<LinkTarget> getTargets() {
		return targets;
	}

	public String getIdentifier() {
		return identifier;
	}

}
