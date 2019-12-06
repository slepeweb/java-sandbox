package com.slepeweb.ifttt.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueManager<T> {

	private final int maxLength;	
	private List<T> buffer;
	
	public QueueManager (int size) {
		this.buffer = new ArrayList<T>(size);
		this.maxLength = size;
	}
	
	public QueueManager<T> add(T member) {
		this.buffer.add(member);
		if (this.buffer.size() > maxLength) {
			this.buffer.remove(0);
		}
		return this;
	}
	
	public int size() {
		return this.buffer.size();
	}
	
	public List<T> getBuffer() {
		return buffer;
	}
	
	public List<T> getBufferReversed() {
		List<T> reversed = new ArrayList<T>(getBuffer());
		Collections.reverse(reversed);
		return reversed;
	}
	
	public void setBuffer(List<T> buffer) {
		this.buffer = buffer;
	}
	
}
