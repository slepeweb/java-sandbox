package com.slepeweb.site.anc.bean.svg;

import com.slepeweb.site.anc.bean.Person;

public class Hyperlink extends Coord {
	
	private static String ANCHOR_FORMAT_STR = "<a xlink:href=\"%s\">%s</a>";
	private static String TSPAN_FORMAT_STR = "<tspan x=\"%d\" dy=\"1.2em\">%s</tspan>";
	private Person person;

	public Hyperlink(int a, int b, Person p) {
		super(a, b);
		this.person = p;
	}
	
	public Hyperlink(Coord c, Person p) {
		super(c.getX(), c.getY());
		this.person = p;
	}
	
	public boolean isBlank() {
		return this.person == null;
	}
	
	public String getLinkTag() {
		if (! isBlank()) {
			return String.format(ANCHOR_FORMAT_STR, 
					this.person.getItem().getUrl(), getText());
		}
		return "";
	}

	public String getLinkTagWithYears() {
		if (! isBlank()) {
			return String.format(ANCHOR_FORMAT_STR, 
					this.person.getItem().getUrl(), getText(true));
		}
		return "";
	}

	public String getSingleLineTextLinkTag() {
		if (! isBlank()) {
			return String.format(ANCHOR_FORMAT_STR, 
					this.person.getItem().getUrl(), getSingleLineText());
		}
		return "";
	}

	public String getText() {
		return getText(true, false, false);
	}

	public String getText(boolean withYears) {
		return getText(true, false, withYears);
	}

	public String getSubjectText() {
		return getText(true, true, false);
	}

	public String getSingleLineText() {
		return getText(false, false, false);
	}
	
	private String getText(boolean isMultiline, boolean isSubject, boolean withYears) {
		StringBuilder sb = new StringBuilder(String.format("<text x=\"%d\" y=\"%d\"", getX(), getY()));
		
		if (isMultiline) {
			sb.append(" dy=\"0\"");
		}
		
		if (isSubject) {
			sb.append(" class=\"subject\"");
		}
		
		sb.append(">");
		
		String name = getPerson().getName();
		String[] nameParts = name.split("\\s");
		
		if (isMultiline) {
			for (int i = 0; i < min(2, nameParts.length); i++) {
				sb.append(String.format(TSPAN_FORMAT_STR, getX(), nameParts[i]));
			}
			
			if (withYears) {
				boolean isEmpty = getPerson().getBirthYear() == null && getPerson().getDeathYear() == null;
				
				if (! isEmpty) {
					StringBuilder years = new StringBuilder();
					if (getPerson().getBirthYear() != null) {
						years.append(getPerson().getBirthYear());
					}
					years.append(" - ");
					if (getPerson().getDeathYear() != null) {
						years.append(getPerson().getDeathYear());
					}
					sb.append(String.format(TSPAN_FORMAT_STR, getX(), years.toString()));
				}
			}
		}
		else {
			sb.append(name);
		}
		
		return sb.append("</text>").toString();
	}
	
	private int min(int a, int b) {
		return a < b ? a : b;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
