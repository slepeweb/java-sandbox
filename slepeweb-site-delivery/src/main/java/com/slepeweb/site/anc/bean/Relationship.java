package com.slepeweb.site.anc.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemFilter;
import com.slepeweb.cms.bean.Link;

/*
 * The subject can be either person in a relationship. It can be either the 'man' in a
 * relationship, or the other member of the pair. It is the person that has the focus.
 */
public class Relationship {
	
	private static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
	private static final String DATE_TEMPLATE = "01/01/1700";
	private Date marriageDate;
	private String marriagePlace;
	private Person subject, partner;
	private List<Person> children;
	private String error;
	
	public Relationship(Person subject, Person partner) {
		this.subject = subject;
		this.partner = partner;
	}
	
	public Relationship(Person subject, Link partner) {
		this.subject = subject;
		this.partner = new Person(partner.getChild());
		parseData(partner.getData());
	}
	
	@Override
	public String toString() {
		return String.format("%s ==> %s", this.subject.toString(), this.partner.toString());
	}
		
	private void parseData(String data) {
		if (data == null) {
			this.error = "Link data missing. Pattern should be <date> :: <location>";
			return;
		}
		
		String[] parts = data.split("\\:\\:");
		if (parts.length != 2) {
			this.error = "Link data format error. Pattern should be <date> :: <location>";
			return;
		}
		
		char[] date = DATE_TEMPLATE.toCharArray();
		char[] entered = parts[0].trim().toCharArray();
		if (entered.length > date.length) {
			this.error = "Link data format error. Date should be dd/MM/yyyy, or MM/yyyy, or yyyy or nothing";
			return;
		}
		
		int offset = date.length - entered.length;
		for (int i = 0; i < entered.length; i++) {
			date[i + offset] = entered[i];
		}
		
		this.marriageDate = setDate(date.toString());
		this.marriagePlace = parts[1].trim();
	}
	
	public boolean isBlankMarriageDetails() {
		return this.marriageDate == null && isBlank(this.marriagePlace);
	}
	
	public String getMarriageDetails() {
		return getDateAndPlaceDetails(getMarriageDate(), getMarriagePlace());
	}
	
	private String getDateAndPlaceDetails(Date date, String place) {
		StringBuilder sb = new StringBuilder();
		if (date != null) {
			sb.append(SDF.format(date));
		}
		if (! isBlank(place)) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(place);
		}
		return sb.toString();
	}
	
	private Date setDate(String dateStr) {
		if (! isBlank(dateStr)) {
			try {
				return SDF.parse(dateStr);
			}
			catch (Exception e) {
				
			}
		}
		
		return null;
	}
	
	private boolean isBlank(String s) {
		return StringUtils.isBlank(s);
	}
	
	public Date getMarriageDate() {
		return marriageDate;
	}
	
	public String getMarriagePlace() {
		return marriagePlace;
	}

	public Person getPartner() {
		return partner;
	}

	public List<Person> getChildren() {
		if (this.children == null) {
			setChildren();
		}
		return this.children;
	}

	private void setChildren() {
		this.children = new ArrayList<Person>();
		Item p = null;
		
		if (getSubject().isMultiPartnered()) {
			/*
			 * In this situation, where the subject has multiple partners, we examine
			 * the children below the subject, AND the primary person, and draw out the
			 * intersection of the two sets.
			 */
			for (Person childOfSubject : identifyChildren(this.subject.getItem())) {
				for (Person childOfPartner : identifyChildren(this.partner.getItem())) {
					if (childOfPartner.getItem().getId().equals(childOfSubject.getItem().getId())) {
						this.children.add(childOfPartner);
						break;
					}
				}
			}
		}
		else {
			/*
			 * The subject has only one partner, so we just look at the bindings/shortcuts
			 * of the primary person (currently, the 'Male').
			 */
			p = this.subject.isMale() ? this.subject.getItem() : this.partner.getItem();
			this.children.addAll(identifyChildren(p));
		}
	}
	
	private List<Person> identifyChildren(Item p) {
		List<Person> children = new ArrayList<Person>();
		
		for (Item i : p.getBoundItems(new ItemFilter().setTypes(new String[] {"Male", "Female"}))) {
			children.add(new Person(i));
		}
		
		return children;
	}
	
	public Person getSubject() {
		return subject;
	}

	public String getError() {
		return error;
	}
	
}
