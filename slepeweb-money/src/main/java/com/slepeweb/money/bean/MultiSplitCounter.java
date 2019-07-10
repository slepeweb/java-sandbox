package com.slepeweb.money.bean;

public class MultiSplitCounter {
	
	private int splitCount, lastSplitId;

	public int getSplitCount() {
		return splitCount;
	}

	public void setSplitCount(int categoryCount) {
		this.splitCount = categoryCount;
	}

	public int getLastSplitId() {
		return lastSplitId;
	}

	public void setLastSplitId(int lastCategoryId) {
		this.lastSplitId = lastCategoryId;
	}

}
