package com.slepeweb.site.anc.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemFilter;

public class Person {
	
	private static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

	private Date birthDate, deathDate, marriageDate;
	private String firstName, lastName, middleNames;
	private String birthPlace, deathPlace, marriagePlace;
	private Person mother, father, partner;
	private List<Person> children, siblings;
	private boolean male;
	private Boolean partnered;
	private Item item, photo;
	private List<Item> documents, records, gallery;
	
	public Person(Item i) {
		this.item = i;
		this.male = i.getType().getName().equals("Male");
		
		this.birthDate = setDate(i.getFieldValue("birthdate"));
		this.deathDate =setDate(i.getFieldValue("deathdate"));
		this.marriageDate = setDate(i.getFieldValue("marriagedate"));
		this.birthPlace = i.getFieldValue("birthplace");
		this.deathPlace = i.getFieldValue("deathplace");
		this.marriagePlace = i.getFieldValue("marriageplace");
		
		this.lastName = i.getFieldValue("lastname");
		this.firstName = i.getFieldValue("firstname");
		this.middleNames = i.getFieldValue("middlenames");
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
	
//	public String getFullName() {
//		StringBuilder sb = new StringBuilder();
//		if (! isBlank(this.lastName)) {
//			sb.append(this.lastName);
//		}
//		
//		if (! isBlank(this.firstName)) {
//			if (sb.length() > 0) {
//				sb.append(", ");
//			}			
//			sb.append(this.firstName);
//			
//			if (! isBlank(this.middleNames)) {
//				sb.append(" ").append(this.middleNames);
//			}
//		}
//		
//		return sb.toString();
//	}
	
	public boolean isBlankBirthDetails() {
		return this.birthDate == null && isBlank(this.birthPlace);
	}
	
	public boolean isBlankDeathDetails() {
		return this.deathDate == null && isBlank(this.deathPlace);
	}
	
	public boolean isBlankMarriageDetails() {
		getPartner(); // Marriage details are stored against the male partner
		return this.marriageDate == null && isBlank(this.marriagePlace);
	}
	
	public String getBirthDetails() {
		return getDateAndPlaceDetails(this.birthDate, this.birthPlace);
	}
	
	public String getDeathDetails() {
		return getDateAndPlaceDetails(this.deathDate, this.deathPlace);
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
	
	private void setParentage() {
		Item parentItem = this.item.getParent();
		
		if (parentItem != null && ! parentItem.getPath().equals("/")) {
			Person parentPerson = new Person(parentItem);
			Person spouse = parentPerson.getPartner();
			
			if (parentPerson.isMale()) {
				this.father = parentPerson;
				this.mother = spouse;
			}
			else {
				this.mother = parentPerson;
				this.father = spouse;
			}
		}
	}
	
	private void setPartner() {
		this.partnered = new Boolean(false);
		
		List<Item> partners = isMale() ? 
				this.item.getRelatedItems(new ItemFilter().setLinkName("partner")) :
					this.item.getRelatedParents(new ItemFilter().setLinkName("partner"));
					
		if (partners != null && partners.size() > 0) {
			this.partner = new Person(partners.get(0));
			this.partnered = new Boolean(true);
			
			// Use male partner's marriage details
			if (! isMale()) {
				this.marriageDate = this.partner.getMarriageDate();
				this.marriagePlace = this.partner.getMarriagePlace();
			}
		}	
	}
	
	private void setChildren() {
		this.children = new ArrayList<Person>();
		
		if (isMale()) {
			for (Item child : this.item.getBoundItems(new ItemFilter().setTypes(new String[] {"Male", "Female"}))) {
				this.children.add(new Person(child));
			}
		}
		else {
			Person spouse = getPartner();
			if (spouse != null) {
				for (Item child : getPartner().getItem().getBoundItems(new ItemFilter().setTypes(new String[] {"Male", "Female"}))) {
					this.children.add(new Person(child));
				}
			}
		}
	}
	
	private void setSiblings() {
		this.siblings = new ArrayList<Person>();
		for (Item sibling : this.item.getParent().getBoundItems(new ItemFilter().setTypes(new String[] {"Male", "Female"}))) {
			if (! sibling.getPath().equals(this.item.getPath())) {
				this.siblings.add(new Person(sibling));
			}
		}
	}
	
	private boolean isBlank(String s) {
		return StringUtils.isBlank(s);
	}
	
	public boolean isDead() {
		return this.deathDate != null;
	}
	
	public boolean isMarried() {
		return this.marriageDate != null;
	}
	
	public Date getBirthDate() {
		return birthDate;
	}
	
	public Date getDeathDate() {
		return deathDate;
	}
	
	public Date getMarriageDate() {
		return marriageDate;
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
		return birthPlace;
	}
	
	public String getDeathPlace() {
		return deathPlace;
	}
	
	public String getMarriagePlace() {
		return marriagePlace;
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
	
	public List<Person> getChildren() {
		if (this.children == null) {
			setChildren();
		}
		return this.children;
	}
	
	public List<Person> getSiblings() {
		if (this.siblings == null) {
			setSiblings();
		}
		return this.siblings;
	}
	
	public boolean isMale() {
		return male;
	}

	public Item getItem() {
		return item;
	}

	public Person getPartner() {
		if (this.partnered == null) {
			setPartner();
		}

		return partner;
	}

	public boolean isPartnered() {
		return partnered != null && partnered.booleanValue();
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
