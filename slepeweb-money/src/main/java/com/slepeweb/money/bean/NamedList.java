package com.slepeweb.money.bean;

import java.util.List;

public class NamedList<T> {
	private long id;
	private String name;
	private List<T> objects;
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public NamedList(String s, List<T> list) {
		this.name = s;
		this.objects = list;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String letter) {
		this.name = letter;
	}
	
	public List<T> getObjects() {
		return objects;
	}
	
	public void setObjects(List<T> objects) {
		this.objects = objects;
	}
}
