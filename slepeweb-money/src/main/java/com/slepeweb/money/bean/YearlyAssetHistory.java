package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

public class YearlyAssetHistory {

	private List<YearlyAssetStatus> list = new ArrayList<YearlyAssetStatus>();

	public List<YearlyAssetStatus> getList() {
		return list;
	}

	public void setList(List<YearlyAssetStatus> list) {
		this.list = list;
	}
	
	public void add(YearlyAssetStatus status) {
		this.list.add(status);
	}
	
	public boolean isEmpty() {
		return this.list.size() == 0;
	}
	
	public YearlyAssetStatus getGrandTotals() {
		YearlyAssetStatus summary = new YearlyAssetStatus();
		for (YearlyAssetStatus yas : getList()) {
			summary.add(yas);
		}
		
		return summary;
	}
}
