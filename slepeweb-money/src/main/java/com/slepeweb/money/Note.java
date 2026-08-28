package com.slepeweb.money;

import org.apache.commons.lang3.StringUtils;

public class Note {

	private long id;
	private String when, detail;
	private int year;
	
	@Override
	public String toString() {
		return String.format("%s: %s", getWhenN(), this.detail);
	}
	
	public long getId() {
		return id;
	}
	
	public Note setId(long id) {
		this.id = id;
		return this;
	}
	
	public String getWhen() {
		return when;
	}
	
	public Note setWhen(String when) {
		this.when = when;
		return this;
	}
	
	public String getDetail() {
		return detail;
	}
	
	public Note setDetail(String detail) {
		this.detail = detail;
		return this;
	}
	
	public int getYear() {
		return year;
	}
	
	public Note setYear(int year) {
		this.year = year;
		return this;
	}
	
	public String getWhenN() {
		return StringUtils.isNotBlank(this.when) ? this.when : String.valueOf(this.year);
	}
	
}
