package com.slepeweb.cms.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.slepeweb.cms.bean.Link.LinkType;

public class Item extends CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(Item.class);
	private Site site;
	private ItemType type;
	private List<FieldValue> fieldValues;
	private String name, simpleName, path;
	private Timestamp dateCreated, dateUpdated;
	private boolean deleted;
	private Long id;
	private List<Link> links;
	
	public void assimilate(Item i) {
		setName(i.getName());
		setSimpleName(i.getSimpleName());
		setPath(i.getPath());
		setDateCreated(i.getDateCreated());
		setDateUpdated(i.getDateUpdated());
		setDeleted(i.isDeleted());
		setSite(i.getSite());
		setType(i.getType());
		
		// TODO: must assimilate fields and links too ???
	}
	
	public boolean isDefined4Insert() {
		boolean b =  
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getPath()) &&
			(StringUtils.isNotBlank(getSimpleName()) || isRoot()) &&
			getDateCreated() != null &&
			getDateUpdated() != null &&
			getSite() != null &&
			getSite().getId() != null &&
			getType() != null &&
			getType().getId() != null;
		
		if (! b) {
			LOG.warn(compose("Item not fully defined for insert/update", getPath()));
		}
		
		return b;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", getName(), getPath());
	}
	
	public Item save() {
		return getItemService().save(this);
	}
	
	public void delete() {
		getItemService().deleteItem(getId());
	}
	
	public Item setFieldValue(String variable, Object value) {
		FieldValue fv = getFieldValue(variable);
		
		// Field value already exists
		if (fv != null) {
			fv.setValue(value);
			LOG.debug(compose("Updated existing field value", getPath(), value));
			return this;
		}
		
		// New field value
		Field f = getFieldService().getField(variable);
		if (f != null) {
			fv = CmsBeanFactory.getFieldValue();
			fv.setField(f);
			fv.setItemId(getId());
			fv.setValue(value);
			getFieldValues().add(fv);
			LOG.debug(compose("Added new field value", getPath(), value));
		}
		
		return this;
	}
	
	public Item addChild(Item child) {
		return getItemService().save(child);
	}
	
	public List<Item> getBoundItems() {
		return getBoundItems(null);
	}
	
	public List<Item> getBoundItems(String linkName) {
		return getChildItems(LinkType.binding, linkName);
	}
	
	public List<Item> getRelatedItems(String linkName) {
		return getChildItems(LinkType.relation, linkName);
	}
	
	public List<Item> getInlineItems(String linkName) {
		return getChildItems(LinkType.inline, linkName);
	}
	
	private List<Item> getChildItems(LinkType linkType, String linkName) {
		List<Item> list = new ArrayList<Item>();
		for (Link l : getLinks()) {
			if (l.getType() == linkType) {
				if (linkName == null || l.getName().equals(linkName)) {
					list.add(l.getChild());
				}
			}
		}
		return list;
	}
	
	public List<Item> getRelatedItems() {
		return null;
	}
	
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
	
	public List<FieldValue> getFieldValues() {
		if (this.fieldValues == null) {
			this.fieldValues = getFieldValueService().getFieldValues(getId());
		}
		return this.fieldValues;
	}
	
	public FieldValue getFieldValue(String variable) {
		for (FieldValue fv : getFieldValues()) {
			if (fv.getField().getVariable().equals(variable)) {
				return fv;
			}
		}
		
		return null;
	}
	
	public Item setFieldValues(List<FieldValue> fields) {
		this.fieldValues = fields;
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

	public List<Link> getLinks() {
		if (this.links == null) {
			this.links = getLinkService().getLinks(getId());
		}
		return this.links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((simpleName == null) ? 0 : simpleName.hashCode());
		result = prime * result + ((site == null) ? 0 : site.getId().hashCode());
		result = prime * result + ((type == null) ? 0 : type.getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (deleted != other.deleted)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (simpleName == null) {
			if (other.simpleName != null)
				return false;
		} else if (!simpleName.equals(other.simpleName))
			return false;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.getId().equals(other.site.getId()))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.getId().equals(other.type.getId()))
			return false;
		return true;
	}
}
