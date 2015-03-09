package com.slepeweb.site.model;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.cms.bean.Item;

public class SiblingItemPager {
	
	private int displayMax;
	private Item currentItem;
	private List<Item> list = new ArrayList<Item>();
	
	public SiblingItemPager(List<Item> list, Item current, int max) {
		this.list = list;
		this.currentItem = current;
		this.displayMax = max;
	}
	
	public int getStart() {
		int cursor = this.list.indexOf(this.currentItem);
		int start = 0;
		if (cursor > -1) {
			start = cursor - (this.displayMax / 2);
			if (start < 0) {
				start = 0;
			}
		}
		
		return start;
	}
	
	public int getEnd() {
		int end = getStart() + this.displayMax - 1;
		
		if (end > this.list.size()) {
			end = this.list.size();
		}
		
		return end;
	}
	
	public int getTotal() {
		return this.list.size();
	}
	
	public int getCurrent() {
		return this.list.indexOf(this.currentItem);
	}
	
	public List<Item> getList() {
		return list;
	}
	
	public SiblingItemPager setList(List<Item> list) {
		this.list = list;
		return this;
	}

	public int getDisplayMax() {
		return displayMax;
	}

	public void setDisplayMax(int displayMax) {
		this.displayMax = displayMax;
	}

	public Item getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(Item currentItem) {
		this.currentItem = currentItem;
	}
	
}
