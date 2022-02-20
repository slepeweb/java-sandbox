package com.slepeweb.cms.bean.guidance;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AncPartnerLinkValidator implements IValidator {
	public static final String REGEXP = "^[mp]\\.\\s*(\\d{1,2}\\/)?(\\d{1,2}\\/)?(\\d{4})?(,?\\s*)?.*?$";
	public static final Pattern PATTERN = Pattern.compile(REGEXP, Pattern.CASE_INSENSITIVE);

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
		return REGEXP;
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
		if (StringUtils.isBlank(value)) {
			return true;
		}
		
		return PATTERN.matcher(value).matches();
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
		System.out.println(new AncPartnerLinkValidator().getJson());
	}
}

//TODO: Function never called! No validation taking place! Just guidance!
//
//This validation only applies to partner links
//_cms.links.validate.linkdata.anc = function(linkType, linkName, linkData) {
	/* 
	 * Link data on the Ancestry site provides the date and location the relationship
	 * was established, and must be formatted as follows:
	 * 
	 * 	<type>. <date>[, <location>]
	 * 
	 * <type> and <date> are mandatory, <location> is optional.
	 * 
	 * <type> can have 2 possible values, followed by a period:
	 * a) m (married)
	 * b) p (partner)
	 * 
	 * <date> can take one of four possible forms:
	 * a) 01/02/1956 (all components present), or
	 * b)    02/1956 (month and year only), or
	 * c)       1956 (year only)
	 * d)          ? (don't know)
	 * 
	 * <location> can be any text string, and if present, must be separated from <type> and
	 * <date> by a comma.
	 */
	/*
	var error = false;
	var dateStr = "", location = "";
	var day = -1, month = -1, year = -1;
	var debug = "";
	var type = "unspecified";
	
	if (linkType == 'relation' && linkName == 'partner') {
		if (linkData) {
			linkData = linkData.trim();
			
			if (linkData.match(/^[mp]\. [\d\?]/)) {
				type = linkData.substring(0, 1);
				linkData = linkData.substring(2).trim();
				
				var firstCommaIndex = linkData.indexOf(",");
			
				if (firstCommaIndex > -1) {
					// Only interested in checking date part
					dateStr = linkData.substring(0, firstCommaIndex).trim();
					location = linkData.substring(firstCommaIndex + 1).trim();
				}
				else {
					dateStr = linkData;
					location = "";
				}
				
				if (! dateStr.startsWith("?")) {
					var dateParts = dateStr.split("/");
					var len = dateParts.length;
					
					if (len == 1) {
						year = parseInt(dateParts[0]);
					}
					
					if (len == 2) {
						month = parseInt(dateParts[0]);
						year = parseInt(dateParts[1]);
					}
					
					if (len == 3) {
						day = parseInt(dateParts[0]);
						month = parseInt(dateParts[1]);
						year = parseInt(dateParts[2]);
					}
					
					if (len > 3) {
						error = true;
					}
					
					error = 
						error || 
						! _cms.support.isBlankOrInRange(year, 1000, 2020) || 
						! _cms.support.isBlankOrInRange(month, 1, 12) || 
						! _cms.support.isBlankOrInRange(day, 1, 31);
				}
			}
			else {
				debug = "linkdata starting format should be, eg, 'm. ?' or 'm. 1956', etc";
				error = true;
			}
		}
		else {
			debug = "linkdata field is empty";
		}
	}
	else {
		debug = "linkdata is not recognised for " + linkType + "/" + linkName;
	}
	
	var result = {
		ok: ! error,
		type: type,
		day: day,
		month: month,
		year: year,
		location: location,
		debug: debug,
	};
	
	return result;
}
*/
