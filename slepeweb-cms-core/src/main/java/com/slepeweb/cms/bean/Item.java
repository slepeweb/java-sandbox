package com.slepeweb.cms.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.except.ResourceException;

public class Item extends CmsBean {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(Item.class);
	public static String NAME_COPY_EXT = " COPY ";
	public static Pattern NAME_COPY_PATTERN = 
			Pattern.compile("(^.*?" + NAME_COPY_EXT + ")(\\d{1,})$");
	public static String SIMPLENAME_COPY_EXT = "-copy-";
	public static Pattern SIMPLENAME_COPY_PATTERN = 
			Pattern.compile("(^.*?" + SIMPLENAME_COPY_EXT + ")(\\d{1,})$");
	
	private Site site;
	private ItemType type;
	private Template template;
	private List<FieldValue> fieldValues;
	private String name, simpleName, path;
	private Timestamp dateCreated, dateUpdated;
	private boolean deleted, editable = true, published, searchable;
	private Long id = -1L, origId;
	private List<Link> links;
	private List<String> tags;
	private Item parent;
	private int version = 1;

	public boolean isProduct() {
		return false;
	}
	
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
			setEditable(i.isEditable());
			setPublished(i.isPublished());
			setSearchable(i.isSearchable());
			setVersion(i.getVersion());
			
			// Must assimilate fields and links too? 
			// NO - fields and links are loaded when needed.
		}
	}
	
	public boolean isDefined4Insert() {
		boolean b =  
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getPath()) &&
			(StringUtils.isNotBlank(getSimpleName()) || isRoot()) &&
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
		if (this.parent == null) {
			Link l = this.cmsService.getLinkService().getParent(getId());
			if (l != null) {
				// In this case, the 'child' IS the 'parent'
				return l.getChild();
			}
		}
		return this.parent;
	}
	
	@Override
	public String toString() {
		return String.format("(%s) %s: %s", getTemplate() != null ? getTemplate().getName() : "No template", 
				getName(), getPath());
	}
	
	public Item getImage() {
		return getImage("std");
	}
	
	// TODO: if no thumbnail, should we scale the main image?
	public Item getThumbnail() {
		return getImage("thumb");
	}
		
	private Item getImage(String classification) {
		ItemFilter f = new ItemFilter().setLinkNames(new String[] {classification});
		List<Item> images = getInlineItems(f);
		if (images.size() > 0) {
			return images.get(0);
		}
		return null;
	}
	
	public int getLevel() {
		if (getPath().equals("/")) {
			return 0;
		}
		else {
			return getPath().substring(1).split("/").length;
		}
	}
	
	public Item save() throws ResourceException {
		return getItemService().save(this);
	}
	
	public Item save(boolean extended) throws ResourceException {
		return getItemService().save(this, extended);
	}
	
	public void saveFieldValues() throws ResourceException {
		getItemService().saveFieldValues(getFieldValues());
	}
	
	public void saveLinks() throws ResourceException {
		 getItemService().saveLinks(this);
	}
	
	public void delete() {
		getItemService().deleteAllVersions(getOrigId());
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
	
	public Item addChild(Item child) throws ResourceException {
		child.setParent(this);
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
	
	/*
	 * Shortcut items must be treated differently when subject to move. In 
	 * particular, need to know which of possibly many parents is affected.
	 */
	public boolean move(Item currentParent, Item target, boolean shortcut) throws ResourceException {
		return move(currentParent, target, shortcut, "over");
	}
	
	public boolean move(Item currentParent, Item target, boolean shortcut, String mode) 
			throws ResourceException {
		return getCmsService().getItemService().move(this, currentParent, target, shortcut, mode);
	}
	
	public Object[] getCopyDetails() {
		Object[] result = new Object[4];
		String baseName, baseSimplename;
		String test = null;
		Matcher nameMatcher = NAME_COPY_PATTERN.matcher(getName());
		Matcher simplenameMatcher = SIMPLENAME_COPY_PATTERN.matcher(getSimpleName());
		Integer n = -1;
		
		if (simplenameMatcher.matches()) {
			baseSimplename = simplenameMatcher.group(1);
			n = Integer.parseInt(simplenameMatcher.group(2)) + 1;
		}
		else {
			baseSimplename = getSimpleName() + SIMPLENAME_COPY_EXT;
			n = 1;
		}
		
		if (nameMatcher.matches()) {
			baseName = nameMatcher.group(1);
		}
		else {
			baseName = getName() + NAME_COPY_EXT;
		}
		
		String parentPath = getParentPath();
		if (parentPath.equals("/")) {
			parentPath = "";
		}
		
		while (n < 10) {
			test = baseSimplename + n;			
			
			// Does this item already exist?
			if (getSite().getItem(parentPath + "/" + test) == null) {
				result[0] = n;
				result[1] = test;
				result[2] = baseName + n;
				result[3] = SIMPLENAME_COPY_EXT;
				return result;
			}
			n++;
		}
		
		// Failed to find suitable names
		n = 99;
		result[0] = n;
		result[1] = baseSimplename + n;
		result[2] = baseName + n;
		result[3] = SIMPLENAME_COPY_EXT;
		return result;
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
	
	public String getFieldValueResolved(String variable) {
		return getFieldValueResolved(variable, "");
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
	
	public String getFieldValueResolved(String variable, String dflt) {
		FieldValue fv = getFieldValueObj(variable);
		if (fv != null) {
			return fv.getStringValueResolved();
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
				// When creating new items, the parent item might not have been identified at this point
				String parentPath = getParentPath();
				if (parentPath != null) {
					setPath(parentPath.equals("/") ? "/" + this.simpleName : parentPath + "/" + this.simpleName);
				}
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
		return filterLinks(new String[] {LinkType.binding, LinkType.shortcut});
	}

	public List<Link> getInlines() {
		return filterLinks(new String[] {LinkType.inline});
	}

	public List<Link> getRelations() {
		return filterLinks(new String[] {LinkType.relation});
	}
	
	public List<Link> getComponents() {
		return filterLinks(new String[] {LinkType.component});
	}
	
	private List<Link> filterLinks(String[] types) {
		List<Link> list = new ArrayList<Link>();
		for (Link l : getLinks()) {
			for (String type : types) {
				if (l.getType().equals(type)) {
					list.add(l);
				}
			}
		}
		return list;
	}

	public List<Link> getAllLinksBarBindings() {
		return filterLinks(new String[] {
				LinkType.inline, 
				LinkType.relation, 
				LinkType.component, 
				LinkType.shortcut});
	}

	public Item setLinks(List<Link> links) {
		this.links = links;
		return this;
	}
	
	public final List<Item> getBoundItems() {
		return getBoundItems(null);
	}
	
	public List<Item> getRelatedItems() {
		return getRelatedItems(null);
	}
	
	public List<Item> getInlineItems() {
		return getInlineItems(null);
	}
	
	public final List<Item> getBoundItems(ItemFilter filter) {
		return toItems(getBindings(), filter);
	}
	
	public final List<Item> getRelatedItems(ItemFilter filter) {
		return toItems(getRelations(), filter);
	}
	
	public final List<Item> getInlineItems(ItemFilter filter) {
		return toItems(getInlines(), filter);
	}
	
	// TODO: Method has not been tested fully.
	private final List<Item> toItems(List<Link> links, ItemFilter filter) {
		List<Item> list = new ArrayList<Item>();
		boolean linkNameMatch, itemTypeMatch;

		for (Link l : links) {
			if (filter != null) {
				linkNameMatch = itemTypeMatch = true;

				if (filter.getLinkNames() != null) {
					linkNameMatch = matches(filter.getLinkNames(), l.getName());
				}

				if (filter.getTypes() != null) {
					itemTypeMatch = matches(filter.getTypes(), l.getChild().getType().getName());
				}

				if (linkNameMatch && itemTypeMatch) {
					list.add(l.getChild());
				}
			} 
			else {
				list.add(l.getChild());
			}
		}

		return list;
	}
	
	private <T> boolean matches(T[] arr, T target) {
		// Must match ANY element in the array
		for (T ele : arr) {
			if (ele.equals(target)) {
				return true;
			}
		}
		
		// There were NO matching elements in the array
		return false;
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
		result = prime * result + (searchable ? 1231 : 1237);
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
		if (searchable != other.searchable)
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

	public List<String> getTags() {
		if (this.tags == null) {
			this.tags = getTagService().getTagValues(getId());
		}
		return this.tags;
	}

	public String getTagsAsString() {
		return StringUtils.join(getTags(), ",").replaceAll(",", ", ");
	}

	public Item setParent(Item parent) {
		this.parent = parent;
		return this;
	}

	public int getVersion() {
		return version;
	}

	public Item setVersion(int version) {
		this.version = version;
		return this;
	}

	public boolean isEditable() {
		return editable;
	}

	public Item setEditable(boolean editable) {
		this.editable = editable;
		return this;
	}

	public Long getOrigId() {
		return origId;
	}

	public Item setOrigId(Long origId) {
		this.origId = origId;
		return this;
	}
	
	public boolean isPage() {
		return getTemplate() != null;
	}

	public boolean isSearchable() {
		return this.searchable;
	}

	public Item setSearchable(boolean searchable) {
		this.searchable = searchable;
		return this;
	}
}
