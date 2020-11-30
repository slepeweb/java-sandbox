package com.slepeweb.site.anc.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemFilter;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkFilter;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.common.util.DateUtil;

public class Person {
	
	public static final String BOY = "Boy";
	public static final String GIRL = "Girl";

	private String firstName, lastName, middleNames;
	private String birthSummary, deathSummary;
	private Person mother, father;
	private List<Person> siblings;
	private List<Relationship> relationships;
	private boolean boy;
	private Item item, photo;
	private List<Item> documents, records, gallery;
	private Integer birthYear, deathYear;
	private Boolean singlePartnerModel;
	
	public Person(Item i) {
		this.item = i;
		this.boy = i.getType().getName().equals(Person.BOY);
		
		this.birthSummary = i.getFieldValue("birthsummary");
		this.deathSummary = i.getFieldValue("deathsummary");
		
		this.lastName = i.getFieldValue("lastname");
		this.firstName = i.getFieldValue("firstname");
		this.middleNames = i.getFieldValue("middlenames");
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	// First, middle and last names
	public String getFullName() {
		StringBuilder sb = new StringBuilder();
		if (! isBlank(this.firstName)) {
			sb.append(this.firstName);
		}
		
		if (! isBlank(this.middleNames)) {
			sb.append(" ").append(this.middleNames);
		}
		
		if (! isBlank(this.lastName)) {
			sb.append(" ").append(this.lastName);			
		}
		
		if (sb.length() == 0) {
			sb.append(getItem().getName());
		}
		
		return sb.toString();
	}
	
	// First and last names
	public String getName() {
		StringBuilder sb = new StringBuilder();
		if (! isBlank(this.firstName)) {
			sb.append(this.firstName);
		}
		
		if (! isBlank(this.lastName)) {
			sb.append(" ").append(this.lastName);			
		}
		
		if (sb.length() == 0) {
			String[] splits = getItem().getName().split("\\s");
			sb.append(splits[0]);
			if (splits.length > 1) {
				sb.append(" ").append(splits[1]);
			}
		}
		
		return sb.toString();
	}
	
	public String getBirthSummary() {
		return this.birthSummary;
	}
	
	public String getDeathSummary() {
		return this.deathSummary;
	}

	private void setParentage() {
		// This is the primary parent, linked by 'binding'
		Item parentItem = this.item.getParent();
		
		if (
				parentItem != null && 
				! parentItem.getPath().equals("/") && 
				(parentItem.getType().getName().equals(Person.BOY) || parentItem.getType().getName().equals(Person.GIRL))) {
			
			Person parentPerson = new Person(parentItem);
			Person otherParentPerson = null;
			List<Relationship> relationships = parentPerson.getRelationships();
			
			if (relationships.size() == 1) {
				otherParentPerson = relationships.get(0).getPartner();
			}
			else {
				LinkFilter f = new LinkFilter().setLinkType(LinkType.shortcut).setItemType(Person.GIRL);
				Link l = f.filterFirst(this.item.getParentLinks());
				
				if (l != null) {
					otherParentPerson = new Person(l.getChild());
				}
				else {
					f.setLinkType(Person.BOY);
					l = f.filterFirst(this.item.getParentLinks());
					
					if (l != null) {
						otherParentPerson = new Person(l.getChild());
					}
				}
			}
			
			if (parentPerson.isBoy()) {
				this.father = parentPerson;
				this.mother = otherParentPerson;
			}
			else {
				this.father = otherParentPerson;
				this.mother = parentPerson;
			}
		}
	}
	
	private void setRelationships() {
		this.relationships = new ArrayList<Relationship>();
		
		LinkFilter f = new LinkFilter().setName("partner").setLinkType(LinkType.relation);		
		
		// Who is the subject (ie. this) partnered to?
		List<Link> partners = f.filterLinks(this.item.getRelations());
		
		// Who is partnered to the subject?
		for (Link l : f.filterLinks(this.item.getParentLinks(false))) {
			if ( l.getChild().getIdentifier() != this.getItem().getIdentifier()) {
				partners.add(l);
			}
		}
		
		if (partners.size() > 0) {
			for (Link l : partners) {
				this.relationships.add(new Relationship(this, l));
			}
		}
		else {
			// Single parent case:
			Person p = null;
			this.relationships.add(new Relationship(this, p));
		}
		
		// Order relationships by date
		Collections.sort(this.relationships, new Comparator<Relationship>() {
			public int compare(Relationship a, Relationship b) {
				if (a == null || b == null || a.getDate() == null || b.getDate() == null) {
					return 0;
				}
				
				return a.getDate().compareTo(b.getDate());
			}
		});
	}
	
	private void setSiblings() {
		this.siblings = new ArrayList<Person>();
		for (Item sibling : this.item.getParent().getBoundItems(new ItemFilter().setTypes(new String[] {Person.BOY, Person.GIRL}))) {
			if (! sibling.getPath().equals(this.item.getPath())) {
				this.siblings.add(new Person(sibling));
			}
		}
	}
	
	private boolean isBlank(String s) {
		return StringUtils.isBlank(s);
	}
	
	public String getFirstName() {
		if (StringUtils.isBlank(this.firstName)) {
			return getItem().getName().split("\\s")[0];
		}
		return this.firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	

	public String getMiddleNames() {
		return middleNames;
	}
	
	public String getBirthPlace() {
		return birthSummary;
	}
	
	public String getDeathPlace() {
		return deathSummary;
	}
	
	public Person getMother() {
		if (this.mother == null && this.father == null) {
			setParentage();
		}
		return this.mother;
	}
	
	public Person getFather() {
		if (this.mother == null && this.father == null) {
			setParentage();
		}
		return this.father;
	}
	
	public List<Person> getSiblings() {
		if (this.siblings == null) {
			setSiblings();
		}
		return this.siblings;
	}
	
	public List<Person> getChildren(int relationshipId) {
		Relationship r = getRelationship(relationshipId);
		return r == null ? new ArrayList<Person>() : r.getChildren();
	}

	public boolean isBoy() {
		return boy;
	}

	public Item getItem() {
		return item;
	}

	public List<Relationship> getRelationships() {
		if (this.relationships == null) {
			setRelationships();
		}

		return this.relationships;
	}

	public Relationship getRelationship(int id) {
		if (getRelationships().size() > id) {
			return getRelationships().get(id);
		}

		return null;
	}

	public boolean isPartnered() {
		return getRelationships() != null && getRelationships().size() > 0;
	}

	public boolean isMultiPartnered() {
		return getRelationships() != null && getRelationships().size() > 1;
	}
	
	public boolean isSinglePartnerModel() {
		if (this.singlePartnerModel == null) {
			this.singlePartnerModel = true;
			
			if (getRelationships().size() > 1) {
				this.singlePartnerModel = false;
			}
			else if (getRelationships().size() == 1 && getRelationship(0).getPartner() != null) {
				this.singlePartnerModel = getRelationship(0).getPartner().getRelationships().size() <= 1;
			}
		}
		return this.singlePartnerModel;
	}
	
	public Item getPhoto() {
		if (this.photo == null) {
			this.photo = this.item.getImage("passport_photo");
		}
		return this.photo;
	}

	public List<Item> getDocuments() {
		if (this.documents == null) {
			this.documents = this.item.getBoundItems(new ItemFilter().setType("Document"));
		}
		
		return this.documents;
	}

	public List<Item> getGallery() {
		if (this.gallery == null) {
			this.gallery = this.item.getBoundItems(new ItemFilter().setType("Image JPG"));
		}
		
		return this.gallery;
	}
	
	public List<Item> getRecords() {
		if (this.records == null) {
			this.records = this.item.getBoundItems(new ItemFilter().setType("PDF"));
		}
		
		return this.records;
	}

	public Integer getBirthYear() {
		if (this.birthYear == null) {
			this.birthYear = getYearFromSummary(this.birthSummary);
		}
		return birthYear;
	}

	public Integer getDeathYear() {
		if (this.deathYear == null) {
			this.deathYear = getYearFromSummary(this.deathSummary);
		}
		return deathYear;
	}
	
	private Integer getYearFromSummary(String summary) {
		if (StringUtils.isNotBlank(summary)) {
			Date d = DateUtil.parseLooseDateString(summary);
			if (d != null) {
				Calendar c = Calendar.getInstance();;
				c.setTime(d);
				return c.get(Calendar.YEAR);
			}
		}
		return null;
	}

}
