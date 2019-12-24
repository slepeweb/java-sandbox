package com.slepeweb.site.anc.bean.svg;

import com.slepeweb.site.anc.bean.Person;

public class Link extends Coord {
	
	private static String ANCHOR_FORMAT_STR = "<a xlink:href=\"%s\">%s</a>";
	private static String TSPAN_FORMAT_STR = "<tspan x=\"%d\" dy=\"1.2em\">%s</tspan>";
	private Person person;

	public Link(int a, int b, Person p) {
		super(a, b);
		this.person = p;
	}
	
	public String getLinkTag() {
		return String.format(ANCHOR_FORMAT_STR, 
				this.person.getItem().getPath(), getText());
	}

	public String getSingleLineTextLinkTag() {
		return String.format(ANCHOR_FORMAT_STR, 
				this.person.getItem().getPath(), getSingleLineText());
	}

	public String getText() {
		return getText(true, false);
	}

	public String getSubjectText() {
		return getText(true, true);
	}

	public String getSingleLineText() {
		return getText(false, false);
	}
	
	private String getText(boolean isMultiline, boolean isSubject) {
		StringBuilder sb = new StringBuilder(String.format("<text x=\"%d\" y=\"%d\"", getX(), getY()));
		
		if (isMultiline) {
			sb.append(" dy=\"0\"");
		}
		
		if (isSubject) {
			sb.append(" class=\"subject\"");
		}
		
		sb.append(">");
		
		if (isMultiline) {
			sb.append(String.format(TSPAN_FORMAT_STR, getX(), getPerson().getFirstName()));
			sb.append(String.format(TSPAN_FORMAT_STR, getX(), getPerson().getLastName()));
		}
		else {
			sb.append(getPerson().getName());
		}
		
		return sb.append("</text>").toString();
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
