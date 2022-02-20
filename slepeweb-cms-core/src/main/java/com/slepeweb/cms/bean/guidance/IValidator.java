package com.slepeweb.cms.bean.guidance;

import java.util.List;

public interface IValidator {

	String getHeading();
	String getTeaser();
	String getRegExp();
	String getFormat();
	List<ExampleInput> getExamples();
	List<String> getDetails();
	String getJson();
	boolean validate(String value);
	String clean(String value);
}
