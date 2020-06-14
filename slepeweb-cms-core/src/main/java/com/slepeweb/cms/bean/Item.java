package com.slepeweb.cms.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.CmsUtil;

public class Item extends CmsBean {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(Item.class);
	public static String NAME_COPY_EXT = " COPY ";
	public static Pattern NAME_COPY_PATTERN = 
			Pattern.compile("(^.*?" + NAME_COPY_EXT + ")(\\d{1,})$");
	public static String SIMPLENAME_COPY_EXT = "-copy-";
	public static Pattern SIMPLENAME_COPY_PATTERN = 
			Pattern.compile("(^.*?" + SIMPLENAME_COPY_EXT + ")(\\d{1,})$");
	public static final String CONTENT_ROOT_PATH = "/content";	
	
	private Site site;
	private ItemType type;
	private Template template;
	private FieldValueSet fieldValues;
	private String name, simpleName, path;
	private Timestamp dateCreated, dateUpdated;
	private boolean deleted, editable = true, published, searchable;
	private Long id = -1L, origId;
	private List<Link> links, parentLinks;
	private List<String> tags;
	private Item parent;
	private List<Item> relatedParents;
	private int version = 1;
	
	// NOTE: language is not saved in the database; it is assigned on item creation
	private String language = "en";
	
	public String getDefaultSimplename() {
		return String.valueOf(new Date().getTime());
	}

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
			setLanguage(i.getLanguage());
			
			// Must assimilate fields and links too? 
			// NO - fields and links are loaded when needed.
		}
	}
	
	public boolean isDefined4Insert() {
		boolean b =  
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getPath()) &&
			//(StringUtils.isNotBlank(getSimpleName()) || isRoot()) &&
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
			if (getId() != null) {
				Link l = this.cmsService.getLinkService().getParent(getId());
				if (l != null) {
					// In this case, the 'child' IS the 'parent'
					this.parent = l.getChild();
					this.parent.setLanguage(getLanguage());
				}
			}
		}
		return this.parent;
	}
	
	public List<Item> getRelatedParents() {
		return getRelatedItems(null);
	}
	
	public List<Item> getRelatedParents(ItemFilter filter) {
		if (this.relatedParents == null) {
			if (getId() != null) {
				this.relatedParents = new ArrayList<Item>();
				List<Link> list = this.cmsService.getLinkService().getRelatedParents(getId());
				
				if (list != null) {
					if (filter != null) {
						this.relatedParents = filter.filterLinks(list);
					}
					else {				
						for (Link l : list) {
							this.relatedParents.add(l.getChild());
						}
					}
				}
			}
		}
		
		return this.relatedParents;
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
		
	public Item getImage(String classification) {
		ItemFilter f = new ItemFilter().setLinkName(classification);
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
		getItemService().saveFieldValues(getFieldValueSet());
	}
	
	public void saveLinks() throws ResourceException {
		 getItemService().saveLinks(this);
	}
	
	public void delete() {
		getItemService().deleteAllVersions(getOrigId());
	}
	
	public Item setFieldValue(String variable, Object value) {
		return setFieldValue(variable, value, getSite().getLanguage());
	}
	
	public Item setFieldValue(String variable, Object value, String language) {
		FieldValue fv = getFieldValueSet().getFieldValueObj(variable, language);
		
		if (fv == null) {
			Field f = getCmsService().getFieldService().getField(variable);
			fv = CmsBeanFactory.makeFieldValue().
					setItemId(getId()).
					setField(f).
					setValue(value).
					setLanguage(language);
			
			getFieldValueSet().addFieldValue(fv);
		}
		
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
	
	@SuppressWarnings("unlikely-arg-type")
	public Item addInline(Item inline) {
		if (! getLinks().contains(inline)) {
			getLinks().add(toChildLink(inline, LinkType.inline));
		}
		
		return this;
	}
	
	@SuppressWarnings("unlikely-arg-type")
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
	public boolean move(Item currentParent, Item targetParent, Item target, 
			boolean moverIsShortcut) throws ResourceException {
		
		return move(currentParent, targetParent, target, moverIsShortcut, "over");
	}
	
	public boolean move(Item currentParent, Item targetParent, Item target, 
			boolean moverIsShortcut, String mode) throws ResourceException {
		
		return getCmsService().getItemService().
				move(this, currentParent, targetParent, target, moverIsShortcut, mode);
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
		
		// parentPath will be null for root items (ie. site & content)
		if (parentPath != null) {
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
		return CmsUtil.getParentPathFromPath(this);
	}

	// Need a getter to simplify jsp functionality
	public boolean isMediaLoaded() {
		return hasMedia();
	}
	
	public boolean hasMedia() {
		return getCmsService().getMediaService().hasMedia(this);
	}
	
	public boolean isSiteRoot() {
		return getPath().equals("/");
	}
	
	public boolean isContentRoot() {
		return getPath().equals(Item.CONTENT_ROOT_PATH);
	}
	
	public boolean isRoot() {
		return isSiteRoot() || isContentRoot();
	}
	
	public Item setType(ItemType type) {
		this.type = type;
		return this;
	}
	
	public Map<String, FieldValue> getFieldValues() {
		return getFieldValueSet().getFieldValues(getLanguage());
	}
		
	/*
	 * It's too verbose to work with FieldValue objects in JSPs, so this
	 * Map should make it simpler.
	 */
	public Map<String, Object> getFields() {
		return getFieldValueSet().getFields(getLanguage());
	}
	
	public FieldValueSet getFieldValueSet() {
		// If this item is not loaded with field values, get them from the database
		if (this.fieldValues == null) {
			/* If there are no field values defined in the db, this returns an empty list
			 * so that the next time getFieldValues() is called, this method does not repeat
			 * the db lookup.
			 * 
			 * This next call returns field values for all languages on a given item
			 */
			this.fieldValues = getCmsService().getFieldValueService().getFieldValues(getId());
		}
		
		return this.fieldValues;
	}
	
	public String getFieldValue(String variable) {
		return getFieldValue(variable, new StringWrapper(""));
	}
	
	public String getFieldValueResolved(String variable) {
		return getFieldValueResolved(variable, new StringWrapper(""));
	}
	
	public String getFieldValue(String variable, StringWrapper dflt) {
		FieldValue fv = getFieldValueObj(variable);
		if (fv != null) {
			return fv.getStringValue();
		}
		else if (dflt != null) {
			return dflt.getValue();
		}
		return null;
	}
	
	public String getFieldValueResolved(String variable, StringWrapper dflt) {
		FieldValue fv = getFieldValueObj(variable);
		if (fv != null) {
			return fv.getStringValueResolved();
		}
		else if (dflt != null) {
			return dflt.getValue();
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
		return getFieldValueObj(variable, getLanguage());
	}
	
	public FieldValue getFieldValueObj(String variable, String language) {
		return getFieldValueSet().getFallbackFieldValueObj(variable, language);
	}
	
	public Item setFieldValues(FieldValueSet fields) {
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
		return this.simpleName;
	}
	
	public Item setSimpleName(String simpleName) {
		this.simpleName = StringUtils.isBlank(simpleName) ? getDefaultSimplename() : simpleName;
		
		if (getPath() != null && ! isSiteRoot()) {
			refreshPath();
		}
		
		return this;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String getUrl() {
		if (getSite().isMultilingual()) {
			return String.format("/%s%s", getLanguage(), getPath());
		}
		
		return getPath();
	}
	
	public String getUrl(String lang) {
		return String.format("/%s%s", lang, getPath());
	}
	
	public String getLanguagePath() {
		return String.format("/%s%s", this.language, this.path);
	}
	
	public String refreshPath() {
		if (! isSiteRoot()) {
			if (isRoot()) {
				setPath("/" + this.simpleName);
			}
			else {
				/*
				 *  When creating new items, the parent item might not have been identified at this point.
				 *  Follow the link preferably, otherwise work with the specified path of the new item.
				 */
				String parentPath = null;
				
				if (getParent() != null) {
					parentPath = getParent().getPath();
				}
				else {
					parentPath = getParentPath();
				}
				
				if (parentPath != null) {
					setPath(parentPath.equals("/") ? "/" + this.simpleName : parentPath + "/" + this.simpleName);
				}
			}
		}
		return this.path;
	}
	
	public void trash() {
		this.cmsService.getItemService().trashItemAndDirectChildren(this);
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
			
			// Set the language on each linked item
			setLinkLanguage(this.links, getLanguage());
		}
		return this.links;
	}

	public List<Link> getParentLinks() {
		return getParentLinks(false);
	}
	
	public List<Link> getParentLinks(boolean includeBinding) {
		if (this.parentLinks == null) {
			this.parentLinks = getLinkService().getParentLinks(getId());
			
			// Set the language on each linked item
			setLinkLanguage(this.parentLinks, getLanguage());
		}
		
		if (! includeBinding) {
			List<Link> links = new ArrayList<Link>(this.parentLinks.size());
			for (Link l : this.parentLinks) {
				if (! l.getType().equals(LinkType.binding)) {
					links.add(l);
				}
			}
			
			return links;
		}
		
		return this.parentLinks;
	}
	
	private void setLinkLanguage(List<Link> links, String language) {
		for (Link l : links) {
			l.getChild().setLanguage(getLanguage());
		}
	}

	public List<Link> getBindings() {
		return filterLinks(new String[] {LinkType.binding, LinkType.shortcut});
	}

	public List<Link> getBindingsNoShortcuts() {
		return filterLinks(new String[] {LinkType.binding});
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

	public List<Link> getAllLinksShortcutsLast() {
		List<Link> list = getAllLinksBarBindings();
		Collections.sort(list, new Comparator<Link>() {

			@Override
			public int compare(Link a, Link b) {
				boolean aIsShortcut = a.getType().equals(LinkType.shortcut);
				boolean bIsShortcut = b.getType().equals(LinkType.shortcut);
				
				if (!(aIsShortcut ^ bIsShortcut)) {
					return a.getOrdering().compareTo(b.getOrdering());
				}
				else if (aIsShortcut) {
					return 1;
				}
				else if (bIsShortcut) {
					return -1;
				}
				return 0;
			}
			
		});
		
		return list;
	}

	public List<Item> getAllVersions() {
		return getCmsService().getItemService().getAllVersions(getOrigId());
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
	
	private final List<Item> toItems(List<Link> links, ItemFilter filter) {
		if (filter != null) {
			return filter.filterLinks(links);
		}
		
		List<Item> list = new ArrayList<Item>();

		for (Link l : links) {
			list.add(l.getChild());
		}

		return list;
	}
	
	public boolean equalsId(Item other) {
		return getId().equals(other.getId());
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

	public String getLanguage() {
		return language;
	}

	public Item setLanguage(String language) {
		this.language = language;
		return this;
	}
}
