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
	private Long id = -1L;
	private List<Link> links;
	private String mediaUploadFilePath;
	
	public void assimilate(Item i) {
		setName(i.getName());
		setSimpleName(i.getSimpleName());
		setPath(i.getPath());
		setDateCreated(i.getDateCreated());
		setDateUpdated(i.getDateUpdated());
		setDeleted(i.isDeleted());
		setSite(i.getSite());
		setType(i.getType());
		setMediaUploadFilePath(i.getMediaUploadFilePath());
		
		// Must assimilate fields and links too? 
		// NO - fields and links are loaded when needed.
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

		return this;
	}
	
	public Item addChild(Item child) {
		return getItemService().save(child);
	}
	
	// TODO: need a way to set ordering for links, eg 'end of list'
	public Item addInline(Item inline) {
		if (! getLinks().contains(inline)) {
			getLinks().add(toChildLink(inline, LinkType.inline));
		}
		
		return this;
	}
	
	public boolean removeInline(Item inline) {
		return getLinks().remove(toChildLink(inline, LinkType.inline));
	}
	
	// TODO: need a better way to handle ordering - currently hardcoding to 0
	private Link toChildLink(Item i, LinkType lt) {
		return CmsBeanFactory.getLink().
				setParentId(getId()).
				setChild(i).
				setType(lt).
				setName("std").
				setOrdering(0);
	}
	
	public final List<Item> getBoundItems() {
		return getBoundItems(null);
	}
	
	public final List<Item> getBoundItems(String linkName) {
		return getLinkedItems(LinkType.binding, linkName);
	}
	
	public final List<Item> getRelatedItems(String linkName) {
		return getLinkedItems(LinkType.relation, linkName);
	}
	
	public final List<Item> getInlineItems(String linkName) {
		return getLinkedItems(LinkType.inline, linkName);
	}
	
	private final List<Item> getLinkedItems(LinkType linkType, String linkName) {
		List<Item> list = new ArrayList<Item>();
		
		// getLinks() will pull links from the DB if null in this object.
		for (Link l : getLinks()) {
			if (l.getType() == linkType) {
				if (linkName == null || l.getName().equals(linkName)) {
					list.add(l.getChild());
				}
			}
		}
		return list;
	}
	
	public void move(Item newParent) {
		getCmsService().getItemService().move(this, newParent);
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
	
	public FieldValue getFieldValue(String variable) {
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

	public String getMediaUploadFilePath() {
		return mediaUploadFilePath;
	}

	public void setMediaUploadFilePath(String mediaUploadFilePath) {
		this.mediaUploadFilePath = mediaUploadFilePath;
	}

	public Item setPath(String path) {
		this.path = path;
		return this;
	}
}
