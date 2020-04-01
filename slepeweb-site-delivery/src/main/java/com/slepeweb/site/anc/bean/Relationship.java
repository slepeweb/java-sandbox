package com.slepeweb.site.anc.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemFilter;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.common.util.DateUtil;

/*
 * The subject can be either person in a relationship. It can be either the 'man' in a
 * relationship, or the other member of the pair. It is the person that has the focus.
 */
public class Relationship {
	
	private static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
	private static Pattern DATE_PATTERN = Pattern.compile("^.*?(\\d{1,2}/)?(\\d{1,2}/)?(\\d{4}).*$");

	private String summary;
	private Person subject, partner;
	private List<Person> children;
	private Date date;
	
	public Relationship(Person subject, Person partner) {
		this.subject = subject;
		this.partner = partner;
	}
	
	public Relationship(Person subject, Link partner) {
		this.subject = subject;
		this.partner = new Person(partner.getChild());
		this.summary = partner.getData();
		parseDate(partner.getData());
	}
	
	@Override
	public String toString() {
		return String.format("%s ==> %s", this.subject.toString(), this.partner.toString());
	}
	
	private void parseDate(String str) {
		if (str == null) {
			return;
		}
	
		Matcher m = DATE_PATTERN.matcher(str);
		if (m.matches()) {
			Calendar cal = DateUtil.today();
			cal.set(Calendar.DATE, getDatePart(m.group(1), 1));
			cal.set(Calendar.MONTH, getDatePart(m.group(2), 1) - 1);
			cal.set(Calendar.YEAR, getDatePart(m.group(3), 1970));
			this.date = cal.getTime();
		}
	}
	
	private int getDatePart(String s, int dflt) {
		if (s == null) {
			return dflt;
		}
		
		if (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		
		return Integer.parseInt(s);
	}

	public Date getDate() {
		return this.date;
	}
	
	public String getDateStr() {
		if (this.date != null) {
			return SDF.format(this.date);
		}
		return "";
	}
	
	public String getSummary() {
		return this.summary;
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
			 * of the primary person.
			 */
			p = this.subject.isPrimary() ? this.subject.getItem() : this.partner.getItem();
			this.children.addAll(identifyChildren(p));
		}
	}
	
	private List<Person> identifyChildren(Item p) {
		List<Person> children = new ArrayList<Person>();
		
		for (Item i : p.getBoundItems(new ItemFilter().setTypes(new String[] {Person.PRIMARY, Person.PARTNER}))) {
			children.add(new Person(i));
		}
		
		return children;
	}
	
	public Person getSubject() {
		return subject;
	}
}
