package com.slepeweb.cms.bean.guidance;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class StdRelatedLinkGuidance implements IGuidance {
	public static final String REGEXP = "^(.*?)$";

	@JsonGetter
	public String getHeading() {
		return "LinkText override";
	}

	@JsonGetter
	public String getTeaser() {
		return "You can override the link text for the child item";
	}

	@JsonGetter
	public String getRegExp() {
		return REGEXP;
	}

	@JsonGetter
	public String getFormat() {
		return "&lt;Any link text - no formatting required.&gt;";
	}

	@JsonGetter
	public List<ExampleInput> getExamples() {
		return new ArrayList<ExampleInput>();
	}

	@JsonGetter
	public List<String> getDetails() {
		return new ArrayList<String>();
	}

	@JsonIgnore
	public boolean validate(String value) {
		return true;
	}
	
	@JsonIgnore
	public String clean(String value) {
		return value;
	}
	
	@JsonIgnore
	public String getJson() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(new StdRelatedLinkGuidance().getJson());
	}
}

