package com.slepeweb.cms.test;

import java.util.List;

public class TestResultSet {

	private List<TestResult> results;
	private boolean success;
	
	public List<TestResult> getResults() {
		return results;
	}
	public void setResults(List<TestResult> results) {
		this.results = results;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
