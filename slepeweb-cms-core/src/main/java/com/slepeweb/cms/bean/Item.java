package com.slepeweb.cms.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.slepeweb.cms.bean.Field.FieldType;

public class Item extends CmsBean {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(Item.class);
	private Site site;
	private ItemType type;
	private Template template;
	private List<FieldValue> fieldValues;
	private String name, simpleName, path;
	private Timestamp dateCreated, dateUpdated;
	private boolean deleted, published;
	private Long id = -1L;
	private List<Link> links;
	
	public void assimilate(Object obj) {
		if (obj instanceof Item) {
			Item i = (Item) obj;
			setName(i.getName());
			setSimpleName(i.getSimpleName());
			setPath(i.getPath());
			setDateCreated(i.getDateCreated());
			setDateUpdated(i.getDateUpdated());
			setDeleted(i.isDeleted());
			setSite(i.getSite());
			setType(i.getType());
			setTemplate(i.getTemplate());
			setPublished(i.isPublished());
			
			// Must assimilate fields and links too? 
			// NO - fields and links are loaded when needed.
		}
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
	
	public void resetDateUpdated() {
		setDateUpdated(new Timestamp(System.currentTimeMillis()));
	}
	
	public String getNodeHierarchy() {
		List<Long> list = new ArrayList<Long>();
		list.add(getId());
		Item climber = this, parent;
		
		while ((parent = climber.getParent()) != null) {
			list.add(parent.getId());
			climber = parent;
		}
		
		Collections.reverse(list);
		StringBuilder sb = new StringBuilder();

		for (Long id : list) {
			sb.append("/").append(id);
		}
				
		return sb.toString();
	}
	
	public Item getParent() {
		Link l = this.cmsService.getLinkService().getParent(getId());
		if (l != null) {
			// In this case, the 'child' IS the 'parent'
			return l.getChild();
		}
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("(%s) %s: %s", getTemplate() != null ? getTemplate().getName() : "No template", 
				getName(), getPath());
	}
	
	public int getLevel() {
		if (getPath().equals("/")) {
			return 0;
		}
		else {
			return getPath().substring(1).split("/").length;
		}
	}
	
	public Item save() {
		return getItemService().save(this);
	}
	
	public Item save(boolean extended) {
		return getItemService().save(this, extended);
	}
	
	public void saveFieldValues() {
		getItemService().saveFieldValues(getFieldValues());
	}
	
	public void saveLinks() {
		 getItemService().saveLinks(this);
	}
	
	public void delete() {
		getItemService().deleteItem(getId());
	}
	
	public Item setFieldValue(String variable, Object value) {
		FieldValue fv = getFieldValueObj(variable);
		
		// Field value already exists
		if (fv != null) {
			fv.setValue(value);
			LOG.debug(compose("Updated existing field value", getPath(), value));
			return this;
		}

		return this;
	}
	
	public Item addChild(Item child) {
		return getItemService().save(child);
	}
	
	public Item addInline(Item inline) {
		if (! getLinks().contains(inline)) {
			getLinks().add(toChildLink(inline, LinkType.inline));
		}
		
		return this;
	}
	
	public Item addRelation(Item relation) {
		if (! getLinks().contains(relation)) {
			getLinks().add(toChildLink(relation, LinkType.relation));
		}
		
		return this;
	}
	
	public boolean removeInline(Item inline) {
		return getLinks().remove(toChildLink(inline, LinkType.inline));
	}
	
	public boolean removeRelation(Item relation) {
		return getLinks().remove(toChildLink(relation, LinkType.relation));
	}
	
	private Link toChildLink(Item i, String linkType) {
		return CmsBeanFactory.makeLink().
				setParentId(getId()).
				setChild(i).
				setType(linkType).
				setName("std").
				setOrdering(0); // Arbitrary value
	}
	
	public final List<Item> getBoundItems() {
		return getBoundItems(null);
	}
	
	public final List<Item> getBoundItems(String linkName) {
		return toItems(getBindings(), linkName);
	}
	
	public final List<Item> getRelatedItems(String linkName) {
		return toItems(getRelations(), linkName);
	}
	
	public final List<Item> getInlineItems(String linkName) {
		return toItems(getInlines(), linkName);
	}
	
	private final List<Item> toItems(List<Link> links, String linkName) {
		List<Item> list = new ArrayList<Item>();
		for (Link l : links) {
			if (linkName == null || l.getName().equals(linkName)) {
				list.add(l.getChild());
			}
		}
		return list;
	}
	
	public boolean move(Item target) {
		return move(target, "over");
	}
	
	public boolean move(Item target, String mode) {
		return getCmsService().getItemService().move(this, target, mode);
	}
	
	public String getParentPath() {
		if (! isRoot()) {
			int c = getPath().lastIndexOf("/");
			if (c > 0) {
				return getPath().substring(0, c);
			}
			return "/";
		}
		
		// A null parent means that this item is a root item
		return null;
	}

	public boolean hasMedia() {
		return getCmsService().getMediaService().hasMedia(this);
	}
	
	public List<Item> getRelatedItems() {
		return getRelatedItems(null);
	}
	
	public List<Item> getInlineItems() {
		return getInlineItems(null);
	}
	
	public boolean isSiteRoot() {
		return getPath().equals("/");
	}
	
	public boolean isRoot() {
		return getPath().equals("/") ||
				(getType().getName().equals(ItemType.CONTENT_FOLDER_TYPE_NAME) && getPath().lastIndexOf("/") == 0);
	}
	
	public Item setType(ItemType type) {
		this.type = type;
		return this;
	}
	
	public Map<String, FieldValue> getFieldValuesMap() {
		Map<String, FieldValue> map = new HashMap<String, FieldValue>();
		for (FieldValue fv : getFieldValues()) {
			map.put(fv.getField().getVariable(), fv);
		}
		return map;
	}
	
	/*
	 * It's too verbose to work with FieldValue objects in JSPs, so this
	 * Map should make it simpler.
	 */
	public Map<String, Object> getFields() {
		Map<String, Object> map = new HashMap<String, Object>();
		Object o;
		
		for (FieldValue fv : getFieldValues()) {
			if (fv.getField().getType() == FieldType.integer) {
				o = fv.getIntegerValue();
			}
			else if (fv.getField().getType() == FieldType.date) {
				o = fv.getDateValue();
			}
			else {
				o = fv.getValue();
			}
			
			map.put(fv.getField().getVariable(), o);
		}

		return map;
	}
	
	public List<FieldValue> getFieldValues() {
		// If this item is not loaded with field values, get them from the database
		if (this.fieldValues == null) {
			/* If there are no field values defined in the db, this returns an empty list
			 * so that the next time getFieldValues() is called, this method does not repeat
			 * the db lookup.
			 */
			setFieldValues(getCmsService().getFieldValueService().getFieldValues(getId()));
		}
		return this.fieldValues;
	}
	
	public String getFieldValue(String variable) {
		return getFieldValue(variable, "");
	}
	
	public String getFieldValue(String variable, String dflt) {
		FieldValue fv = getFieldValueObj(variable);
		if (fv != null) {
			return fv.getStringValue();
		}
		else if (dflt != null) {
			return dflt;
		}
		return null;
	}
	
	public Integer getIntFieldValue(String variable) {
		return getIntFieldValue(variable, null);
	}
	
	public Integer getIntFieldValue(String variable, Integer dflt) {
		FieldValue fv = getFieldValueObj(variable);
		if (fv != null) {
			return fv.getIntegerValue();
		}
		return dflt;
	}
	
	public Timestamp getDateFieldValue(String variable) {
		return getDateFieldValue(variable, null);
	}
	
	public Timestamp getDateFieldValue(String variable, Timestamp dflt) {
		FieldValue fv = getFieldValueObj(variable);
		if (fv != null) {
			return fv.getDateValue();
		}
		return dflt;
	}
	
	public FieldValue getFieldValueObj(String variable) {
		if (getFieldValues() != null) {
			for (FieldValue fv : getFieldValues()) {
				if (fv.getField().getVariable().equals(variable)) {
					return fv;
				}
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
		if (getPath() != null && ! isSiteRoot()) {
			refreshPath();
		}
		return this;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String refreshPath() {
		if (! isSiteRoot()) {
			if (isRoot()) {
				setPath("/" + this.simpleName);
			}
			else {
				String parentPath = getParentPath();
				setPath(parentPath.equals("/") ? "/" + this.simpleName : parentPath + "/" + simpleName);
			}
		}
		return this.path;
	}
	
	public void trash() {
		this.cmsService.getItemService().trashItem(getId());
	}
	
	public void restore() {
		this.cmsService.getItemService().restoreItem(getId());
	}
	
	public String getUrl() {
		return new StringBuilder("//").append(getSite().getHostname()).append(getPath()).toString();
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

	public List<Link> getBindings() {
		return filterLinks(LinkType.binding);
	}

	public List<Link> getInlines() {
		return filterLinks(LinkType.inline);
	}

	public List<Link> getRelations() {
		return filterLinks(LinkType.relation);
	}
	
	public List<Link> getComponents() {
		return filterLinks(LinkType.component);
	}
	
	private List<Link> filterLinks(String type) {
		List<Link> list = new ArrayList<Link>();
		for (Link l : getLinks()) {
			if (l.getType().equals(type)) {
				list.add(l);
			}
		}
		return list;
	}

	public List<Link> getAllLinksBarBindings() {
		List<Link> list = getInlines();
		list.addAll(getRelations());
		list.addAll(getComponents());
		return list;
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
		result = prime * result + ((site == null) ? 0 : site.getId().hashCode());
		result = prime * result + ((type == null) ? 0 : type.getId().hashCode());
		result = prime * result + ((dateUpdated == null) ? 0 : dateUpdated.hashCode());
		result = prime * result + (published ? 1231 : 1237);
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
		if (published != other.published)
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
		
		if (dateUpdated == null) {
			if (other.dateUpdated != null)
				return false;
		} else if (!dateUpdated.equals(other.dateUpdated))
			return false;
		
		return true;
	}

	public Item setPath(String path) {
		this.path = path;
		return this;
	}

	public Template getTemplate() {
		return template;
	}

	public Item setTemplate(Template template) {
		this.template = template;
		return this;
	}

	public boolean isPublished() {
		return published;
	}

	public Item setPublished(boolean published) {
		this.published = published;
		return this;
	}
}
