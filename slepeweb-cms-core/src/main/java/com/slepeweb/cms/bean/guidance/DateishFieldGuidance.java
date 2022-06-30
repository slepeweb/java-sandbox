package com.slepeweb.cms.bean.guidance;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slepeweb.cms.bean.Dateish;

/*
 * This class has been written with a view that its data would also be required at
 * the front-end as a json string, but it turns out that that would not be necessary.
 * At some point in the future, the jackson annotations might be removed ...
 */

@Component
public class DateishFieldGuidance implements IGuidance {

	@JsonGetter
	public String getHeading() {
		return "Approximate Date";
	}

	@JsonGetter
	public String getTeaser() {
		return "This field provides an approximate date to an event, or a precise date, if available.";
	}

	@JsonGetter
	public String getRegExp() {
		return Dateish.REGEXP;
	}

	@JsonGetter
	public String getFormat() {
		return "&lt;yyyy/mm/dd&gt; | &lt;yyyy/mm&gt; | &lt;yyyy&gt; | &lt;&gt;";
	}

	@JsonGetter
	public List<ExampleInput> getExamples() {
		List<ExampleInput> list = new ArrayList<ExampleInput>();
		
		list.add(new ExampleInput(
				"1960/3/6",
				"Fully specified date. Months and dates can be represented by single digits."));
		
		list.add(new ExampleInput(
				"1960/3",
				"Certain the date was in March that year."));

		list.add(new ExampleInput(
				"1960",
				"Only certain about the year."));


		list.add(new ExampleInput(
				"1960a",
				"Year is best approximation. Useful for sorting purposes."));

		return list;
	}

	@JsonGetter
	public List<String> getDetails() {
		List<String> list = new ArrayList<String>();
		
		list.add("In all cases, an 'a' can be appended to the date, to indicate that it is a best approximation");		
		list.add("Date must lie within the range 1100 to 2100");				
		list.add("If no clue about the date, then leave field blank.");
		
		return list;
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

	@JsonIgnore
	public boolean validate(String s) {		
		if (StringUtils.isBlank(s)) {
			return true;
		}
						
		return new Dateish(s).isValid();
	}
	
	@JsonIgnore
	public String clean(String value) {
		return new Dateish(value).toString();
	}
	
	public static void main(String[] args) throws Exception {
		IGuidance ig = null;
		try {
			Class<?> clazz = Class.forName("com.slepeweb.cms.bean.guidance.DateishFieldValidator");
			ig = (IGuidance) clazz.getDeclaredConstructor().newInstance();			
		}
		catch (Exception e) {
			System.out.println("Failed to identify guidance: " + e.getMessage());
			return;
		}
		
		test("1956", ig);
		test("1956/6", ig);
		test("1956/6s", ig);
		test("1956/6/20", ig);
		test("1956/6/20a", ig);
		test("1956/6/20/a", ig);
		test("", ig);
		test("1956/2/31", ig);

	}
	
	private static void test(String s, IGuidance ig) {
		Dateish d = new Dateish(s);
		System.out.println(String.format("Input [%s] produces [%s] : %s", s, d.toString(), ig.validate(s) ? "ok" : "fail"));
	}
}
