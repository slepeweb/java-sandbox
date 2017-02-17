package com.slepeweb.cms.bean;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;

public class Site extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String name, shortname;
	private Long id;
		
	public void assimilate(Object obj) {
		if (obj instanceof Site) {
			Site s = (Site) obj;
			setName(s.getName());
			setShortname(s.getShortname());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getShortname());
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s)", getName(), getShortname());
	}
	
	public Site save() throws MissingDataException, DuplicateItemException {
		return getSiteService().save(this);
	}
	
	public void delete() {
		getSiteService().deleteSite(this);
	}
	
	public Item getItem(String path) {
		return getItemService().getItem(getId(), path);
	}
	
	public Item getItem(Long id) {
		return getItemService().getItem(id);
	}
	
	public Item getTaggedItem(String tagname) {
		return getTagService().getTaggedItem(getId(), tagname);
	}
	
	public List<Template> getAvailableTemplates() {
		return getCmsService().getTemplateService().getAvailableTemplates(getId());
	}
	
	public List<Template> getAvailableTemplates(Long itemTypeId) {
		return getCmsService().getTemplateService().getAvailableTemplates(getId(), itemTypeId);
	}
	
	public List<ItemType> getAvailableItemTypes() {
		return getCmsService().getItemTypeService().getAvailableItemTypes();
	}
	
	public String getName() {
		return name;
	}
	
	public Site setName(String name) {
		this.name = name;
		return this;
	}
	
	public Long getId() {
		return id;
	}
	
	public Site setId(Long id) {
		this.id = id;
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((shortname == null) ? 0 : shortname.hashCode());
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
		Site other = (Site) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (shortname == null) {
			if (other.shortname != null)
				return false;
		} else if (!shortname.equals(other.shortname))
			return false;
		return true;
	}

	public String getShortname() {
		return shortname;
	}

	public Site setShortname(String shortname) {
		this.shortname = shortname;
		return this;
	}

}
