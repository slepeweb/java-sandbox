package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.slepeweb.cms.bean.ItemUpdateRecord.Action;

public class ItemUpdateHistory {
	private static Logger LOG = Logger.getLogger(ItemUpdateHistory.class);
	public static int MAX_SIZE = 10;
	
	private List<ItemUpdateRecord> lifo = new ArrayList<ItemUpdateRecord>(MAX_SIZE);
	private int pointer = -1;

	public ItemUpdateRecord push(Item i, Item j, ItemUpdateRecord.Action a) {
		ItemUpdateRecord rec = new ItemUpdateRecord(i, j, a);
		
		// Remove earliest entry if list length exceeds maximum
		if (getSize() > MAX_SIZE - 1) {
			this.lifo.remove(0);
		}
		
		// Before adding a record to the list, first discards all entries after the pointer
		for (int n = getSize() - 1; n > this.pointer; n--) {
			this.lifo.remove(n);
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
			return this.lifo.get(i);
		}
		
		return null;
	}
	
	public boolean isItemUpdateRecordAvailable() {
		return isValidPointer(this.pointer);
	}

	public boolean isNextItemUpdateRecordAvailable() {
		return isValidPointer(this.pointer + 1);
	}

	public boolean isPreviousItemUpdateRecordAvailable() {
		return isValidPointer(this.pointer - 1);
	}

	private boolean isValidPointer(int p) {
		return p >= 0 && p < getSize();
	}
	
	public void undoCompleted() {
		this.pointer--;
		LOG.info("Undo completed ... " + getStatus());
	}
	
	public void redoCompleted() {
		this.pointer++;
		LOG.info("Redo completed ... " + getStatus());
	}
	
	public List<ItemUpdateRecord> getList() {
		return this.lifo;
	}
	
	public int getPointer() {
		return this.pointer;
	}
	
	public void clear() {
		this.lifo.clear();
		this.pointer = -1;
		LOG.info("List cleared ... " + getStatus());
	}
	
	private String getStatus() {
		return String.format("Size: %d, Pointer: %d, Target: %s", 
			getSize(), getPointer(), 
			getItemUpdateRecord() != null ? getItemUpdateRecord().getBefore().getName() : "n/a");
	}
	
	public int getSize() {
		return this.lifo.size();
	}
	
	// For testing
	public static void main(String[] args) {
		ItemUpdateHistory h = new ItemUpdateHistory();
		make(h, "One");
		make(h, "Two"); 
		make(h, "Three"); 
		
		h.undoCompleted();
		h.redoCompleted();
		h.undoCompleted();
		make(h, "Four");
	}
	
	private static void make(ItemUpdateHistory h, String name) {
		h.push(new Item().setName(name), new Item().setName(name), Action.core);
	}
}
