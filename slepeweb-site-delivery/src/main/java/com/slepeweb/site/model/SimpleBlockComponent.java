package com.slepeweb.site.model;

import java.util.List;

public class SimpleBlockComponent extends Component {
	private static final long serialVersionUID = 1L;
	private List<Image> images;
	private List<LinkTarget> targets;

	public List<Image> getImages() {
		return images;
	}

	public SimpleBlockComponent setImages(List<Image> media) {
		this.images = media;
		return this;
	}

	public List<LinkTarget> getTargets() {
		return targets;
	}

	public void setTargets(List<LinkTarget> targets) {
		this.targets = targets;
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
