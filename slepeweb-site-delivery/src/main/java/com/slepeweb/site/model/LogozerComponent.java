package com.slepeweb.site.model;

import com.slepeweb.cms.bean.Link;

public class LogozerComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	//private static Logger LOG = Logger.getLogger(LogozerComponent.class);

	private int numCells = 8;
	private long fadeInterval, imageReplacementInterval, nextCellOffset;
	private String cellClass;

	public LogozerComponent setup(Link l) {
		super.setup(l);		
		return this;
	}
	
	public int getNumImages() {
		return getComponents().size();
	}
	
	public Integer[] getCellIds() {
		return toIdList(this.numCells);
	}
	
	public Integer[] getImageIds() {
		return toIdList(getComponents().size());
	}
	
	private Integer[] toIdList(int num) {
		Integer[] ids = new Integer[num];
		for (int i = 0; i < num; i++) {
			ids[i] = i + 1;
		}
		return ids;
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

	public String getCellClass() {
		return cellClass;
	}

	public LogozerComponent setCellClass(String cellClass) {
		this.cellClass = cellClass;
		return this;
	}

	public long getNextCellOffset() {
		return nextCellOffset;
	}

	public LogozerComponent setNextCellOffset(long nextCellOffset) {
		this.nextCellOffset = nextCellOffset;
		return this;
	}

}
