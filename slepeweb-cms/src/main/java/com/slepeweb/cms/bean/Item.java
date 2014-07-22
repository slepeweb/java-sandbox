package com.slepeweb.cms.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Item extends CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Site site;
	private ItemType type;
	private List<Field> fields;
	private String name, simpleName, path;
	private Timestamp dateCreated, dateUpdated;
	private boolean deleted;
	private Long id;
	
	public void assimilate(Item i) {
		setName(i.getName());
		setSimpleName(i.getSimpleName());
		setPath(i.getPath());
		setDateCreated(i.getDateCreated());
		setDateUpdated(i.getDateUpdated());
		setDeleted(i.isDeleted());
		setSite(i.getSite());
		setType(i.getType());
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getPath()) &&
			(StringUtils.isNotBlank(getSimpleName()) || isRoot()) &&
			getDateCreated() != null &&
			getDateUpdated() != null &&
			getSite() != null &&
			getSite().getId() != null &&
			getType() != null &&
			getType().getId() != null;
	}
	
	// TODO
	public List<Item> getBoundItems() {
		return null;
	}
	
	// TODO
	public List<Item> getRelatedItems() {
		return null;
	}
	
	// TODO
	public List<Item> getInlineItems() {
		return null;
	}
	
	public boolean isRoot() {
		return getPath() != null && getPath().equals("/");
	}
	
	public Item setType(ItemType type) {
		this.type = type;
		return this;
	}
	
	public List<Field> getFields() {
		return fields;
	}
	
	public Item setFields(List<Field> fields) {
		this.fields = fields;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public Item setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getSimpleName() {
		return simpleName;
	}
	
	public Item setSimpleName(String simpleName) {
		this.simpleName = simpleName;
		return this;
	}
	
	public String getPath() {
		return path;
	}
	
	public Item setPath(String path) {
		this.path = path;
		return this;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	public Item setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}
	
	public Long getId() {
		return id;
	}
	
	public Item setId(Long id) {
		this.id = id;
		return this;
	}
	
	public Item setSite(Site site) {
		this.site = site;
		return this;
	}
	
	public Timestamp getDateCreated() {
		return dateCreated;
	}
	
	public Item setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
	public Timestamp getDateUpdated() {
		return dateUpdated;
	}
	
	public Item setDateUpdated(Timestamp dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

	public Site getSite() {
		return site;
	}

	public ItemType getType() {
		return type;
	}
}
