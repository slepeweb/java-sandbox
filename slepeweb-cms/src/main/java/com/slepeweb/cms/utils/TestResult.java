package com.slepeweb.cms.utils;

public class TestResult {
	private int id, result = 1;
	private String title, notes;
	
	public int getId() {
		return id;
	}
	
	public TestResult setId(int id) {
		this.id = id;
		return this;
	}
	
	public String getResult() {
		return result == 0 ? "Fail" : "Pass";
	}
	
	public TestResult setResult(int result) {
		this.result = result;
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public TestResult setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public TestResult setNotes(String notes) {
		this.notes = notes;
		return this;
	}
	
	public TestResult pass() {
		this.result = 1;
		return this;
	}
	
	public TestResult fail() {
		this.result = 0;
		return this;
	}
}
