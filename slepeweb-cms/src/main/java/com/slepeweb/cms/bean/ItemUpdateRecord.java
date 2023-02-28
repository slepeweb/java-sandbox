package com.slepeweb.cms.bean;

public class ItemUpdateRecord {
	private Item before, after;
	private long origId;
	private Action action;
	
	public ItemUpdateRecord(Item i, Item j, Action a) {
		this.before = i;
		this.after = j;
		this.action = a;
		this.origId = i.getOrigId();
	}
	
	public Item getBefore() {
		return before;
	}

	public Item getAfter() {
		return after;
	}

	public Action getAction() {
		return action;
	}

	public long getOrigId() {
		return origId;
	}

	public enum Action {
		core, field, links, media, move, none
	}
}
