package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.slepeweb.cms.bean.ItemUpdateRecord.Action;

public class ItemUpdateHistory {
	private static Logger LOG = Logger.getLogger(ItemUpdateHistory.class);
	public static int MAX_SIZE = 1; // If you change this, also attend to MediaFileServiceImpl.MAX_FILES
	public static int MIN_POINTER = -1;
	
	private List<ItemUpdateRecord> lifo = new ArrayList<ItemUpdateRecord>(MAX_SIZE);
	private int pointer = MIN_POINTER;

	public ItemUpdateRecord push(Item i, Item j, ItemUpdateRecord.Action a) {
		ItemUpdateRecord rec = new ItemUpdateRecord(i, j, a);
		
		// Before adding a record to the list, first discards all entries after the pointer.
		// (If the pointer is at the end of the list, then no records will be discarded.)
		for (int n = getSize() - 1; n > this.pointer; n--) {
			this.lifo.remove(n);
		}		
		
		// Make space in list if it has reached (or exceeded) maximum length
		if (getSize() >= MAX_SIZE) {
			this.lifo.remove(0);
		}
		
		// Then add the new record, and re-set the pointer to the end of the list
		this.lifo.add(rec);
		this.pointer = getSize() - 1;
		
		LOG.info("ItemUpdateRecord appended ... " + getStatus());
		
		return rec;
	}

	public ItemUpdateRecord getItemUpdateRecord() {
		return getItemUpdateRecord(this.pointer);
	}

	public ItemUpdateRecord getNextItemUpdateRecord() {
		return getItemUpdateRecord(this.pointer + 1);
	}

	private ItemUpdateRecord getItemUpdateRecord(int i) {
		if (isValidPointer(i)) {
			ItemUpdateRecord r = this.lifo.get(i);
			if (r.getAction() != Action.none) {
				return r;
			}
		}
		
		return null;
	}
	
	private boolean isValidPointer(int p) {
		return p > MIN_POINTER && p < getSize() && p < MAX_SIZE;
	}
	
	public void undoCompleted() {
		this.pointer--;
		
		if (this.pointer < MIN_POINTER) {
			LOG.error("(min) Pointer error ... " + getStatus());
			this.pointer = MIN_POINTER;
		}
		else {
			LOG.info("Undo completed ... " + getStatus());
		}
	}
	
	public void redoCompleted() {
		this.pointer++;
		
		if (this.pointer >= getSize() || this.pointer >= MAX_SIZE) {
			LOG.error("(max) Pointer error ... " + getStatus());
			this.pointer = getSize() - 1;
		}
		else {
			LOG.info("Redo completed ... " + getStatus());
		}
	}
	
	public List<ItemUpdateRecord> getList() {
		return this.lifo;
	}
	
	public int getPointer() {
		return this.pointer;
	}
	
	public void clear() {
		this.lifo.clear();
		this.pointer = MIN_POINTER;
		LOG.info("List cleared ... " + getStatus());
	}
	
	private String getStatus() {
		String name = "n/a", action = "n/a";
		if (getItemUpdateRecord() != null) {
			name = getItemUpdateRecord().getBefore().getName();
			action = getItemUpdateRecord().getAction().name();
		}
		
		return String.format("Size: %d, Pointer: %d, Action: %s, Target: %s", 
			getSize(), getPointer(), action, name); 
	}
	
	public int getSize() {
		return this.lifo.size();
	}
	
	// For testing
	public static void main(String[] args) {
		ItemUpdateHistory h = new ItemUpdateHistory();
		make(h, "One", 1);
		make(h, "Two", 2); 
		make(h, "Three", 3); 
		
		h.undoCompleted();
		h.redoCompleted();
		h.undoCompleted();
		make(h, "Four", 4);
	}
	
	private static void make(ItemUpdateHistory h, String name, long id) {
		h.push(new Item().setName(name).setOrigId(id), new Item().setName(name).setOrigId(id), Action.core);
	}
}
