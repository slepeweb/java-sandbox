package com.slepeweb.site.model;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.cms.bean.Link;

public class SectionComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	private Image image;
	private List<LinkTarget> targets;

	public SectionComponent setup(Link l) {
		super.setup(l);
		this.targets = new ArrayList<LinkTarget>();
		return this;
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

}
