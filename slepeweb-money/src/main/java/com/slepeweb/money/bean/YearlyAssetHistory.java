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
}
