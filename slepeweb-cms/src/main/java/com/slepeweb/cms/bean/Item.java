package com.slepeweb.cms.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.service.ItemService;

public class Item implements Serializable {
	private static final long serialVersionUID = -1107189949889224015L;
	private Site site;
	private ItemType type;
	private List<Field> fields;
	private String name, simpleName, path;
	private Timestamp dateCreated, dateUpdated;
	private boolean deleted;
	private Long id;
	
	private transient ItemService itemService;
	
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
	
	public boolean isDefined() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getSimpleName()) &&
			StringUtils.isNotBlank(getPath()) &&
			getDateCreated() != null &&
			getDateUpdated() != null &&
			getSite() != null &&
			getSite().getId() != null &&
			getType() != null &&
			getType().getId() != null
			;
	}
	
	public ItemType getType() {
		if (! getType().isDefined()) {
			this.type = this.itemService.getItemType(this);
		}
		return type;
	}
	public void setType(ItemType type) {
		this.type = type;
	}
	public List<Field> getFields() {
		return fields;
	}
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSimpleName() {
		return simpleName;
	}
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Site getSite() {
		if (! this.site.isDefined()) {
			this.site = this.itemService.getSite(this);
		}
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public Timestamp getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Timestamp getDateUpdated() {
		return dateUpdated;
	}
	public void setDateUpdated(Timestamp dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
}
