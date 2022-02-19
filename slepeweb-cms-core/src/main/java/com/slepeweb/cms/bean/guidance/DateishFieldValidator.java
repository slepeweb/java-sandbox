package com.slepeweb.cms.bean.guidance;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slepeweb.cms.bean.Dateish;

/*
 * This class has been written with a view that its data would also be required at
 * the front-end as a json string, but it turns out that that would not be necessary.
 * At some point in the future, the jackson annotations might be removed ...
 */

public class DateishFieldValidator implements IValidator {

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
				"1960s",
				"Year is best approximation. Useful for sorting purposes."));

		return list;
	}

	@JsonGetter
	public List<String> getDetails() {
		List<String> list = new ArrayList<String>();
		
		list.add("In all cases, an 's' can be appended to the date, to indicate that it is a best approximation, " +
				"and is provided mainly for sorting purposes");
		
		list.add("Date must lie within the range 1100 to 2100");
				
		list.add("If no clue about the date, then leave field blank.");
		
		return list;
	}
	
	@JsonIgnore
	public String getJson() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}

	@JsonIgnore
	public boolean validate(String value) {
		boolean ok = true;
		
		if (StringUtils.isBlank(value)) {
			return true;
		}
		
		if (Dateish.PATTERN.matcher(value).matches()) {
			// Check values are within specific ranges
			Dateish d = new Dateish(value);
			
			if (d.getYear() != null) {
				ok = ok && d.getYear() > 1100 && d.getYear() < 2100;
				
				if (ok && d.getMonth() != null) {
					ok = ok && d.getMonth() > 0 && d.getMonth() <= 12;
					
					if (ok && d.getDay() != null) {
						ok = ok && d.getDay() > 0 && d.getDay() <= 31;
					}
				}
			}
		}
		else {
			ok = false;
		}
		
		return ok;
	}
	
	@JsonIgnore
	public String clean(String value) {
		return new Dateish(value).toString();
	}
	
	public static void main(String[] args) throws Exception {
		try {
			Class<?> clazz = Class.forName("com.slepeweb.cms.bean.guidance.DateishFieldValidator");
			IValidator ig = (IValidator) clazz.getDeclaredConstructor().newInstance();
			System.out.println(ig.getJson());
		}
		catch (Exception e) {
			System.out.println("Failed to identify guidance: " + e.getMessage());
		}

	}
}
