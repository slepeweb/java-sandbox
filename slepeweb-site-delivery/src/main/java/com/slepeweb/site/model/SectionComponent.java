package com.slepeweb.site.model;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.cms.bean.Link;

public class StandardComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	private List<Image> images;
	private List<LinkTarget> targets;

	public StandardComponent setup(Link l) {
		super.setup(l);
		
		// Images
		setImages(new ArrayList<Image>());
		for (Link ll : l.getChild().getInlines()) {
			if (! ll.getName().equals(Image.BACKGROUND)) {
				getImages().add(new Image(ll));
			}
		}
		
		// Link targets
		setTargets(new ArrayList<LinkTarget>());
		for (Link ll : l.getChild().getRelations()) {
			getTargets().add(new LinkTarget(ll.getChild()));
		}
		
		return this;
	}
	
	public String toString() {
		return String.format("StandardComponent (%s): %s", getType(), getHeading());
	}
	
	public List<Image> getImages() {
		return images;
	}

	public StandardComponent setImages(List<Image> media) {
		this.images = media;
		return this;
	}

	public List<LinkTarget> getTargets() {
		return targets;
	}

	public StandardComponent setTargets(List<LinkTarget> targets) {
		this.targets = targets;
		return this;
	}

	public Image getBackgroundImage() {
		return getImage(true);
	}
	
	public Image getMainImage() {
		return getImage(false);
	}
	
	private Image getImage(boolean targetIsBackground) {
		boolean isBackgroundImage;
		for (Image img : getImages()) {
			isBackgroundImage = img.getType().equals(Image.BACKGROUND);
			if (
					(targetIsBackground && isBackgroundImage) ||
					(! targetIsBackground && ! isBackgroundImage)) {
				
				return img;
			}
		}
		return null;
	}

}
