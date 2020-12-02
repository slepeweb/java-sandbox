package com.slepeweb.site.anc.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkFilter;
import com.slepeweb.common.util.DateUtil;

/*
 * The subject can be either person in a relationship. It can be either the 'boy' in a
 * relationship, or the other member of the pair. It is the subject that has the focus.
 */
public class Relationship {
	
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
		this.date = DateUtil.parseLooseDateString(partner.getData());
	}
	
	@Override
	public String toString() {
		return String.format("%s ==> %s", this.subject.toString(), this.partner.toString());
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public String getDateStr() {
		if (this.date != null) {
			return DateUtil.DATE_PATTERN_B.format(this.date);
		}
		return null;
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
		 * have the same 'mother'.
		 */
		if (! getSubject().isSinglePartnerModel()) {
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
					if (matches(childOfPartner, childOfSubject)) {
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
	
	private boolean matches(Person a, Person b) {
		return a.getItem().getIdentifier() == b.getItem().getIdentifier();
	}
	
	
	private void union() {
		this.children.addAll(identifyChildren(this.subject.getItem()));
		
		if (this.partner != null) {
			boolean isDuplicate;
			for (Person childOfPartner : identifyChildren(this.partner.getItem())) {
				isDuplicate = false;
				for (Person c : this.children) {
					if (matches(childOfPartner, c)) {
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
	
	private List<Person> identifyChildren(Item j) {
		List<Person> children = new ArrayList<Person>();
		LinkFilter f = new LinkFilter().setItemTypes(new String[] {Person.BOY, Person.GIRL});
		
		for (Item i : f.filterItems(j.getBindings())) {
			children.add(new Person(i));
		}
		
		return children;
	}
	
	public Person getSubject() {
		return subject;
	}
}
