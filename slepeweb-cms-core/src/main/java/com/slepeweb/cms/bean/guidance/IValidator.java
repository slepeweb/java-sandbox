package com.slepeweb.cms.bean.guidance;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IValidator {

	String getHeading();
	String getTeaser();
	String getRegExp();
	String getFormat();
	List<ExampleInput> getExamples();
	List<String> getDetails();
	String getJson() throws JsonProcessingException;
	boolean validate(String value);
	String clean(String value);
}
