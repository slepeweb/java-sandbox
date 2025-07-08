package com.slepeweb.cms.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.slepeweb.cms.constant.FieldName;
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
	
	// These properties pertain to access control
	private RequestPack requestPack = new RequestPack((User) null);
	private User owner;
	private Boolean accessible;
	
	private Long id = -1L, origId;
	private Long ownerId;
	private List<Link> links, parentLinks;
	private List<Tag> tags;
	private Item parent;
	private int version = 1;
	
	protected List<Media> allMedia;
	
	// Added for convenience, and only applicable when adding a new item, in order that
	// we can choose between binding and component when adding a new item.
	// This properties are NOT set when retrieving items from the db.
	// It would have been neater to extend the Item class, but on investigation, that
	// would be too disruptive to the code.
	private Link link4newItem;
	
	public Item setAllMedia(List<Media> allMedia) {
		this.allMedia = allMedia;
		return this;
	}

	public String getDefaultSimplename() {
		if (StringUtils.isNotBlank(getName())) {
			return getName().toLowerCase().replaceAll("[\\s\\W]", "");
		}
		else {
			return String.valueOf(new Date().getTime());
		}
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
			setOwnerId(i.getOwnerId());
			
			// Must assimilate fields and links too? What about tags?
			// NO - fields and links are loaded when needed.
		}
	}
	
	public boolean isDefined4Insert() {
		boolean b =  
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getPath()) &&
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
		
		while ((parent = climber.getOrthogonalParent()) != null) {
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
	
	public Item getOrthogonalParent() {
		if (this.parent == null) {
			Link l = getOrthogonalParentLink();
			if (l != null) {
				this.parent = l.getChild();
				this.parent.setRequestPack(getRequestPack());
			}
		}
		return this.parent;
	}
	
	// Get an item from the same site as the same user.
	// Let the caller decide what to do if inaccessible
	public Item getItem(String path) {
		Item i = getCmsService().getItemService().getItem(getSite().getId(), path);
		if (i != null) {
			i.setRequestPack(getRequestPack());
		}
		return i;
	}
	
	@Override
	public String toString() {
		return String.format("(%s) %s: %s", getTemplate() != null ? getTemplate().getName() : "No template", 
				getName(), getPath());
	}
	
	public Item getFirstInlineImage() {
		LinkFilter f = new LinkFilter().setMimeTypePatterns("image/.*");
		return f.filterFirstItem(getInlines());
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
	
	public void saveFieldValues() throws ResourceException {
		getItemWorkerService().saveFieldValues(this);
	}
	
	public void saveLinks() throws ResourceException {
		 getItemWorkerService().saveLinks(this);
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
	
	public boolean removeInline(Item inline) {
		return getLinks().remove(CmsBeanFactory.toChildLink(this, inline, LinkType.inline));
	}
	
	public boolean removeRelation(Item relation) {
		return getLinks().remove(CmsBeanFactory.toChildLink(this, relation, LinkType.relation));
	}
	
	public String getParentPath() {
		return CmsUtil.getParentPathFromPath(this);
	}

	public boolean isMainMediaWithBinaryContent() {
		return isMediaWithBinaryContent(false);
	}
	
	public boolean isThumbnailWithBinaryContent() {
		return isMediaWithBinaryContent(true);
	}
	
	private boolean isMediaWithBinaryContent(boolean forThumbnail) {
		if (getType().isMedia()) {
			Media m = getCmsService().getMediaService().getMedia(getId(), forThumbnail);
			return m != null && m.isBinaryContentLoaded();
		}
		return false;
	}

	public boolean hasMedia() {
		return getCmsService().getMediaService().hasMedia(this);
	}
	
	public boolean hasThumbnail() {
		return getCmsService().getMediaService().hasThumbnail(this);
	}
	
	public boolean isSiteRoot() {
		return StringUtils.isNotBlank(this.path) && this.path.equals("/");
	}
	
	public boolean isContentRoot() {
		return StringUtils.isNotBlank(this.path) && this.path.equals(Item.CONTENT_ROOT_PATH);
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
	
	// TODO: redundant functionality, HOWEVER, lots of old code is using it.
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
	
	// This requires name property to have previously been set
	public Item setSimpleName(String simpleName) {
		this.simpleName = StringUtils.isBlank(simpleName) ? getDefaultSimplename() : simpleName;
		return this;
	}
	
	/*
	 * For a simplename change on an existing item, you also have to consider the affect on the
	 * item path property.
	 */
	public Item setSimpleNameAndPath(String simpleName) {
		setSimpleName(simpleName);
		
		/*
		 * If this item's id is set, then this code assumes that it already exists in
		 * the database, and therefore by changing the simplename, we need to also
		 * change the path.
		 * 
		 * If this item is being newly created, and is no yet bound to a parent, then changing
		 * the simplename should not attempt to change the path.
		 */
		if (getId() > 0 && ! isSiteRoot()) {
			refreshPath();
		}
		
		return this;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String getMiniPath() {
		return String.format("/$_%d", getOrigId());
	}
	
	public String getUrl() {
		String path = isXrequest() ? getMiniPath() : getPath();
		return getSite().isMultilingual() ? 
				String.format("/%s%s", getLanguage(), path) : path;
	}
	
	public String getUrl(String lang) {
		return String.format("/%s%s", lang, getPath());
	}
	
	public String getLanguagePath() {
		return String.format("/%s%s", this.requestPack.getLanguage(), this.path);
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
				
				if (getOrthogonalParent() != null) {
					parentPath = getOrthogonalParent().getPath();
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
		return getLinks(false);
	}
	
	public List<Link> getLinks(boolean filterInaccessibles) {
		if (this.links == null) {
			this.links = getLinkService().getLinks(getId());
			
			/*
			 * In CMSe and CMSd, you need read access to be able to see an item.
			 * In CMSe, you additionally need write access to update an item.
			 * Accessibility can only be determined given user details.
			 */			
			if (filterInaccessibles && getUser() != null) {
				filterReadableLinks(this.links);
			}
			
			// Embellish linked items with additional data: user, language, foreign site flag, etc.
			setRequestPack(this.links);
		}
		return this.links;
	}
	
	public List<Link> getParentLinks() {
		return getParentLinks(false);
	}
	
	public List<Link> getParentLinksIncludingBindings() {
		return getParentLinks(true);
	}
	
	public List<Link> getParentLinks(boolean includeBinding) {
		if (this.parentLinks == null) {
			this.parentLinks = getLinkService().getParentLinks(getId());
			
			// Set the language on each linked item
			setRequestPack(this.parentLinks);
		}
		
		if (! includeBinding) {
			List<Link> links = new ArrayList<Link>(this.parentLinks.size());
			for (Link l : this.parentLinks) {
				if (! l.getType().equals(LinkType.binding) && ! l.getType().equals(LinkType.component)) {
					links.add(l);
				}
			}
			
			return links;
		}
		
		return this.parentLinks;
	}
	
	public Link getOrthogonalParentLink() {
		return filterOrthogonalParentLink();
	}
	
	public List<Link> getBindings() {
		return filterLinks(new String[] {LinkType.binding});
	}

	@Deprecated
	public List<Link> getBindingsNoShortcuts() {
		return getBindings();
	}

	public List<Link> getInlines() {
		return filterLinks(new String[] {LinkType.inline});
	}
	
	public Media getMedia() {
		return filterMedia(false);
	}
	
	public Media getThumbnail() {
		return filterMedia(true);
	}
	
	protected Media filterMedia(boolean thumbnailRequired) {
		for (Media m : getAllMedia()) {
			
			if (thumbnailRequired && m.isThumbnail()) {
				return m;
			}
			else if (! thumbnailRequired && ! m.isThumbnail()) {
				return m;
			}
		}
		
		return null;
	}

	public List<Media> getAllMedia() {
		if (this.allMedia == null) {
			this.allMedia = this.cmsService.getMediaService().getAllMedia(getId());
		}
		
		return this.allMedia;
	}
	
	public List<Link> getRelations() {
		return filterLinks(new String[] {LinkType.relation});
	}
	
	public List<Link> getComponents() {
		return  filterLinks(new String[] {LinkType.component});
	}
	
	private List<Link> filterLinks(String[] linkTypes) {
		List<Link> list = new ArrayList<Link>();
		for (Link l : getLinks()) {
			if (matches(l.getType(), linkTypes)) {
				list.add(l);
			}
		}
		return list;
	}
	
	private Link filterOrthogonalParentLink() {
		// No point continuing if this is a root item
		if (this.isRoot()) {
			return null;
		}
		
		String[] targets = new String[] {LinkType.binding, LinkType.component};
		List<Link> plinks = getParentLinksIncludingBindings();
		List<Link> orthos = new ArrayList<Link>();
		
		// Filter out orthogonal link(s), binding or component - there should only be one!
		for (Link l : plinks) {
			if (matches(l.getType(), targets)) {
				orthos.add(l);
			}
		}
		
		if (orthos.size() > 1) {
			LOG.error(String.format("DB Integrity Error; Child item [%d] has more than one orthogonal parent", getId()));
			return null;
		}
		else if (orthos.size() == 0) {
			LOG.error(String.format("DB Integrity Error; Child item [%d] has NO parent", getId()));
			return null;
		}
		else {
			return orthos.get(0);
		}
	}
	
	private boolean matches(String target, String[] options) {
		if (options != null) {
			for (String s : options) {
				if (s.equals(target)) {
					return true;
				}
			}
			
			return false;
		}
		
		// Nothing to match target against, so target IS considered valid
		return true;
	}

	public List<Link> getNonOrthogonalLinks() {
		return filterLinks(new String[] {
				LinkType.inline, 
				LinkType.relation, 
				LinkType.shortcut});
	}

	public List<Link> getOrthogonalLinks() {
		return filterLinks(getOrthogonalLinkTypes());
	}
	
	public String[] getOrthogonalLinkTypes() {
		return new String[] {
				LinkType.binding, 
				LinkType.component};
	}

	public List<Item> getAllVersions() {
		return getCmsService().getItemService().getAllVersions(getOrigId());
	}
	
	public Item setLinks(List<Link> links) {
		this.links = links;
		return this;
	}
	
	public final List<Item> getBoundItems() {
		return CmsUtil.toItems(getOrthogonalLinks());
	}
	
	public final List<Item> getBoundPages() {
		List<Item> list = new ArrayList<Item>();
		for (Item i : CmsUtil.toItems(getOrthogonalLinks())) {
			if (i.isPage()) {
				list.add(i);
			}
		}
		return list;
	}
	
	public List<Item> getRelatedItems() {
		return CmsUtil.toItems(getRelations());
	}
	
	public List<Item> getInlineItems() {
		return CmsUtil.toItems(getInlines());
	}
	
	public boolean equalsId(Item other) {
		return getId().equals(other.getId());
	}
	
	// This test of equality was introduced to support use of shortcuts in a delivery context
	public boolean equalsIdentifier(Item other) {
		return getIdentifier() == other.getIdentifier();
	}
	
	public boolean isShortcut() {
		return false;
	}
	
	public boolean isXrequest() {
		return this.requestPack.isXrequest();
	}

	public Item setXrequest(boolean foreigner) {
		this.requestPack.setXrequest(foreigner);
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((template == null) ? 0 : template.getId().hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
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
		
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.getId().equals(other.template.getId()))
			return false;
		
		if (ownerId == null) {
			if (other.ownerId != null)
				return false;
		} else if (!ownerId.equals(other.ownerId))
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

	public Item setPath(Item parent) {
		this.path = String.format("%s/%s", parent.isSiteRoot() ? "" : parent.getPath(), getSimpleName());
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

	public List<Tag> getTags() {
		if (this.tags == null) {
			this.tags = getTagService().getTags4Item(getId());
		}
		return this.tags;
	}
	
	public Item setTags(List<Tag> l) {
		this.tags = l;
		return this;
	}

	public List<String> getTagValues() {
		return Tag.toValues(getTags());
	}

	public String getTagsAsString() {
		StringBuilder sb = new StringBuilder();
		for (Tag t : getTags()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(t.getValue());
		}
		return sb.toString();
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

	public Link getLink4newItem() {
		return link4newItem;
	}

	public Item setLink4newItem(Link link4newItem) {
		this.link4newItem = link4newItem;
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
	
	public boolean isHiddenFromNav() {
		String fv = this.getFieldValue(FieldName.HIDE_FROM_NAV);		
		return fv != null && fv.equalsIgnoreCase("yes");
	}

	public Item setSearchable(boolean searchable) {
		this.searchable = searchable;
		return this;
	}

	public String getLanguage() {
		return this.requestPack.getLanguage();
	}

	public Item setLanguage(String language) {
		this.requestPack.setLanguage(language);
		return this;
	}
	
	public long getIdentifier() {
		return getId();
	}
	
	public User getUser() {
		return this.requestPack.getUser();
	}

	public Item setUser(User user) {
		this.requestPack.setUser(user);
		return this;
	}

	public RequestPack getRequestPack() {
		return this.requestPack;
	}

	public Item setRequestPack(RequestPack r) {
		this.requestPack = r;
		return this;
	}

	// Does this item pass access rules set in db table 'access'
	public boolean isAccessible() {
		if (this.accessible == null) {
			this.accessible = getCmsService().getSiteAccessService().isAccessible(this);
		}
		
		return this.accessible;
	}
	
	public Long getOwnerId() {
		return ownerId;
	}

	public Item setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
		return this;
	}

	public List<String> getSolrKeys() {
		List<String> list = new ArrayList<String>();
		for (String lang : getSite().getAllLanguages()) {
			list.add(String.format("%s-%s", getId(), lang));
		}
		return list;
	}
	
	public User getOwner() {
		if (this.owner == null && this.ownerId != null) {
			this.owner = getCmsService().getUserService().get(this.ownerId);
		}
		
		return this.owner;
	}
	
	public String getTempMediaFilepath(boolean isThumbnail) {
		return getCmsService().getMediaFileService().getTempMediaFilepath(this, isThumbnail);
	}	

	
	/*
	 * IFF this site is designated as a secured site ...
	 * This method will filter out any child items in a list of links should the user
	 * not have read access to them. This method would be used on sites where it was
	 * known that not all items would be visible to all users.
	 */
	private List<Link> filterReadableLinks(List<Link> links) {
		if (getCmsService().isEditorialContext() || getSite().isSecured()) {
			if (links != null && links.size() > 0) {
				Iterator<Link> iter = links.iterator();
				Link l;
				
				while (iter.hasNext()) {
					l = iter.next();
					l.getChild().setRequestPack(getRequestPack());
					if (! l.getChild().isAccessible()) {
						iter.remove();
					}
				}
			}
		}
		
		return links;
	}
	
	// Applies to both child AND parent links
	private void setRequestPack(List<Link> links) {
		if (links != null) {
			Item i;
			
			for (Link l :  links) {
				i = l.getChild();
				i.setRequestPack(getRequestPack());
			}
		}
	}

}
