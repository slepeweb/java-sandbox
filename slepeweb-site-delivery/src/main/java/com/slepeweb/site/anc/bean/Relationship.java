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
 * The subject can be either person in a relationship. It can be either the 'boy' in a
 * relationship, or the other member of the pair. It is the subject that has the focus.
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
		
		/*
		 * In 1-to-many relationships, the content author MUST first add a child item
		 * to the item representing the 'father' role, and then create a shortcut from the 'mother' 
		 * item to the same child item. In this way, the parents of a child are clearly identified. 
		 * 
		 * In a 1-to-1 relationship, the content author only needs to attach child items to the
		 * 'father' item. The code makes the assumption in this case that all the child items
		 * had the same 'mother'.
		 */
		if (getSubject().isMultiPartnered()) {
			/*
			 * So, in the situation where the subject has multiple partners, we examine
			 * the children below the subject, AND the partner, and draw out the
			 * _intersection_ of the two sets. 
			 */
			intersect();
		}
		else {
			/*
			 * The subject has only one partner, so we just look at the bindings/shortcuts
			 * of each person, and add them together (even though normal practise would be
			 * to attach the child items to either one parent item or the other).
			 */
			union();
		}
	}
	
	private void intersect() {
		if (this.partner != null) {
			for (Person childOfSubject : identifyChildren(this.subject.getItem())) {
				for (Person childOfPartner : identifyChildren(this.partner.getItem())) {
					if (childOfPartner.getItem().equalsId(childOfSubject.getItem())) {
						this.children.add(childOfPartner);
						break;
					}
				}
			}
		}
		else {
			this.children.addAll(identifyChildren(this.subject.getItem()));
		}
	}
	
	private void union() {
		this.children.addAll(identifyChildren(this.subject.getItem()));
		
		if (this.partner != null) {
			boolean isDuplicate;
			for (Person childOfPartner : identifyChildren(this.partner.getItem())) {
				isDuplicate = false;
				for (Person c : this.children) {
					if (childOfPartner.getItem().equalsId(c.getItem())) {
						isDuplicate = true;
						break;
					}
				}
				
				if (! isDuplicate) {
					this.children.add(childOfPartner);
				}
			}
		}
	}
	
	private List<Person> identifyChildren(Item p) {
		List<Person> children = new ArrayList<Person>();
		
		for (Item i : p.getBoundItems(new ItemFilter().setTypes(new String[] {Person.BOY, Person.GIRL}))) {
			children.add(new Person(i));
		}
		
		return children;
	}
	
	public Person getSubject() {
		return subject;
	}
}
