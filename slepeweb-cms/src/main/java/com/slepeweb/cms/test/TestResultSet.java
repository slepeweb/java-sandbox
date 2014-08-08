package com.slepeweb.cms.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.slepeweb.cms.utils.LogUtil;

public class TestResultSet {

	private static Logger LOG = Logger.getLogger(TestResultSet.class);
	private List<TestResult> results = new ArrayList<TestResult>();
	private Map<Integer, TestResult> map = new HashMap<Integer, TestResult>();
	private String heading;
	
	public TestResultSet(String heading) {
		this.heading = heading;
	}
	
	public TestResultSet register(int id, String title) {	
		return register(id, title, "");
	}
	
	public TestResultSet register(int id, String title, String expected) {		
		TestResult r = new TestResult().setId(id).setTitle(title).setExpected(expected);
		this.results.add(r);
		this.map.put(id, r);
		return this;
	}
	
	public boolean isComplete() {
		for (TestResult t : this.results) {
			if (! t.isExecuted()) {
				return false;
			}
		}
		return true;
	}
	
	public TestResult execute(int id) {
		TestResult tr =  this.map.get(id);
		if (tr == null) {
			tr = new TestResult();
			LOG.error(LogUtil.compose("No such test registered", id));
		}
		
		tr.setExecuted();
		return tr;
	}
	
	public List<TestResult> getResults() {
		return results;
	}
	
	public void setResults(List<TestResult> results) {
		this.results = results;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}
}
