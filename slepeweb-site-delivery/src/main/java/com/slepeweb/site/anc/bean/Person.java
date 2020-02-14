package com.slepeweb.site.anc.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemFilter;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkFilter;
import com.slepeweb.cms.bean.LinkType;

public class Person {
	
	public static final String PRIMARY = "Primary";
	public static final String PARTNER = "Partner";

	private String firstName, lastName, middleNames;
	private String birthSummary, deathSummary;
	private Person mother, father;
	private List<Person> siblings;
	private List<Relationship> relationships;
	private boolean primary;
	private Item item, photo;
	private List<Item> documents, records, gallery;
	
	public Person(Item i) {
		this.item = i;
		this.primary = i.getType().getName().equals(Person.PRIMARY);
		
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
		
		return sb.toString();
	}
	
	public String getName() {
		StringBuilder sb = new StringBuilder();
		if (! isBlank(this.firstName)) {
			sb.append(this.firstName);
		}
		
		if (! isBlank(this.lastName)) {
			sb.append(" ").append(this.lastName);			
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
		
		if (parentItem != null && ! parentItem.getPath().equals("/")) {
			Person parentPerson = new Person(parentItem);
			this.father = parentPerson;
			List<Relationship> relationships = parentPerson.getRelationships();
			
			if (relationships.size() == 1) {
				this.mother = relationships.get(0).getPartner();
			}
			else {
				LinkFilter f = new LinkFilter().setLinkType(LinkType.shortcut).setItemType(Person.PARTNER);;
				Link l = f.filterFirst(this.item.getParentLinks());
				
				if (l != null) {
					this.mother = new Person(l.getChild());
				}
			}
		}
	}
	
	private void setRelationships() {
		this.relationships = new ArrayList<Relationship>();
		
		LinkFilter f = new LinkFilter().setName("partner").setLinkType(LinkType.relation);
		
		List<Link> partners = isPrimary() ? 
				f.filterLinks(this.item.getRelations()) :
					f.filterLinks(this.item.getParentLinks());
					
		for (Link l : partners) {
			this.relationships.add(new Relationship(this, l));
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
		for (Item sibling : this.item.getParent().getBoundItems(new ItemFilter().setTypes(new String[] {Person.PRIMARY, Person.PARTNER}))) {
			if (! sibling.getPath().equals(this.item.getPath())) {
				this.siblings.add(new Person(sibling));
			}
		}
	}
	
	private boolean isBlank(String s) {
		return StringUtils.isBlank(s);
	}
	
	public String getFirstName() {
		return firstName;
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

	public boolean isPrimary() {
		return primary;
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
}
