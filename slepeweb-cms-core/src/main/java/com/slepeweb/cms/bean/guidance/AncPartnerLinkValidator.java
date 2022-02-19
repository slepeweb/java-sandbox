package com.slepeweb.cms.bean.guidance;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AncPartnerLinkValidator implements IValidator {

	@JsonGetter
	public String getHeading() {
		return "Partnership";
	}

	@JsonGetter
	public String getTeaser() {
		return "This field provides the type of partnership, and the date it was established.";
	}

	@JsonGetter
	public String getRegExp() {
		return "^[mp]\\.\\s*(\\d{1,2}\\/)?(\\d{1,2}\\/)?(\\d{4})?(,?\\s*)?.*?$";
	}

	@JsonGetter
	public String getFormat() {
		return "&lt;partnership code&gt;. &lt;date established (optional)&gt;, &lt;relevant location (optional)&gt;";
	}

	@JsonGetter
	public List<ExampleInput> getExamples() {
		List<ExampleInput> list = new ArrayList<ExampleInput>();
		
		list.add(new ExampleInput(
				"m. 26/09/1981, Hemel Hempstead",
				"Married on given date at given location."));
		
		list.add(new ExampleInput(
				"p. 1981",
				"Partnership formed in 1981, exact date not known."));

		return list;
	}

	@JsonGetter
	public List<String> getDetails() {
		List<String> list = new ArrayList<String>();
		list.add("Partnership code can be m (married) or p (partner) followed by a period.");
		list.add("Date can be year (yyyy), or month and year (mm/yyyy), full date (dd/mm/yyyy), or blank if not known.");
		list.add("Location is optional, otherwise must be preceded by a comma UNLESS the date is also blank.");
		
		return list;
	}

	@JsonIgnore
	public boolean validate(String value) {
		return false;
	}
	
	@JsonIgnore
	public String clean(String value) {
		return value;
	}
	
	@JsonIgnore
	public String getJson() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(new AncPartnerLinkValidator().getJson());
	}
}
