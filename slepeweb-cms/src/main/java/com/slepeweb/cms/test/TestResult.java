package com.slepeweb.cms.test;

public class TestResult {
	private int id;
	private String title, expected = "", notes = "";
	private boolean success = true, executed;
	
	public int getId() {
		return id;
	}
	
	public TestResult setId(int id) {
		this.id = id;
		return this;
	}
	
	public String getResult() {
		return ! isExecuted() || ! success ? "Fail" : "Pass";
	}
	
	public TestResult setSuccess(boolean b) {
		this.success = b;
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
		this.success = true;
		return this;
	}
	
	public TestResult fail() {
		this.success = false;
		return this;
	}

	public boolean isExecuted() {
		return executed;
	}
	
	public String getExecutionFlag() {
		return isExecuted() ? "Yes" : "No";
	}

	public TestResult setExecuted(boolean executed) {
		this.executed = executed;
		return this;
	}

	public TestResult setExecuted() {
		this.executed = true;
		return this;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getExpected() {
		return expected;
	}

	public TestResult setExpected(String expected) {
		this.expected = expected;
		return this;
	}
}
