package com.slepeweb.site.model;

import com.slepeweb.cms.bean.Link;

public class LogozerComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	//private static Logger LOG = Logger.getLogger(LogozerComponent.class);

	private int numCells = 8, numUsPerCell = 6;
	private long fadeInterval, imageReplacementInterval;

	public LogozerComponent setup(Link l) {
		super.setup(l);		
		return this;
	}
	
	public int getNumImages() {
		return getComponents().size();
	}
	
	public Integer[] getEmptyCells() {
		Integer[] emptyCells = new Integer[this.numCells - getComponents().size()];
		int start = getComponents().size() + 1;
		for (int i = 0; (i + start) <= this.numCells; i++) {
			emptyCells[i] = start + i;
		}
		return emptyCells;
	}
	
	@Override
	public String toString() {
		return String.format("LogozerComponent (%s)", getName());
	}

	public int getNumCells() {
		return numCells;
	}

	public LogozerComponent setNumCells(int numCells) {
		this.numCells = numCells;
		return this;
	}

	public int getNumUsPerCell() {
		return numUsPerCell;
	}

	public LogozerComponent setNumUsPerCell(int numUsPerCell) {
		this.numUsPerCell = numUsPerCell;
		return this;
	}

	public long getFadeInterval() {
		return fadeInterval;
	}

	public LogozerComponent setFadeInterval(long fadeInterval) {
		this.fadeInterval = fadeInterval;
		return this;
	}

	public long getImageReplacementInterval() {
		return imageReplacementInterval;
	}

	public LogozerComponent setImageReplacementInterval(long imageReplacementInterval) {
		this.imageReplacementInterval = imageReplacementInterval;
		return this;
	}

}
