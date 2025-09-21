package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Host.HostType;
import com.slepeweb.cms.except.ResourceException;

public class Site extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String name, shortname, language, extraLanguages;
	private String[] extraLanguagesArray;
	private Long id;
	private boolean secured = true;
		
	public void assimilate(Object obj) {
		if (obj instanceof Site) {
			Site s = (Site) obj;
			setName(s.getName());
			setShortname(s.getShortname()).
			setLanguage(s.getLanguage()).
			setExtraLanguages(s.getExtraLanguages()).
			setSecured(s.isSecured());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getShortname()) &&
			StringUtils.isNotBlank(getLanguage());
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s)", getName(), getShortname());
	}
	
	public List<String> getAllLanguages() {
		List<String> all = new ArrayList<String>();
		all.add(getLanguage());
		for (int i = 0; i < getExtraLanguagesArray().length; i++) {
			all.add(getExtraLanguagesArray()[i]);
		}
		return all;
	}
	
	public Site save() throws ResourceException {
		return getSiteService().save(this);
	}
	
	public void delete() {
		getSiteService().deleteSite(this);
	}
	
	public Item getItem(String path) {
		return getItemService().getItem(getId(), path);
	}
	
	public Item getContentItem(String relativePath) {
		return getItemService().getItem(getId(), Item.CONTENT_ROOT_PATH + relativePath);
	}
	
	public Item getItem(Long id) {
		return getItemService().getItem(id);
	}
	
	public List<Template> getAvailableTemplates() {
		return getCmsService().getTemplateService().getAvailableTemplates(getId());
	}
	
	public List<Template> getAvailableTemplates(Long itemTypeId) {
		return getCmsService().getTemplateService().getAvailableTemplates(getId(), itemTypeId);
	}

	public List<Template> getAvailableTemplates(User u) {
		List<Template> list = getAvailableTemplates();
		filterAdminTemplates(list, u);
		return list;
	}
	
	public List<Template> getAvailableTemplates(Long itemTypeId, User u) {
		List<Template> list = getAvailableTemplates(itemTypeId);
		filterAdminTemplates(list, u);
		return list;
	}
	
	private void filterAdminTemplates(List<Template> list, User u) {
		if (! u.getRoles(getId()).contains(User.ADMIN)) {
			Iterator<Template> iter = list.iterator();
			Template t;
			
			while (iter.hasNext()) {
				t = iter.next();
				if (t.isAdmin()) {
					iter.remove();
				}
			}
		}
	}
	
	public List<SiteType> getAvailableItemTypes() {
		return getCmsService().getSiteTypeService().get(this.getId());
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
	
	public String getLanguage() {
		return language;
	}

	public Site setLanguage(String defaultLanguage) {
		this.language = defaultLanguage;
		return this;
	}

	public String getExtraLanguages() {
		return extraLanguages;
	}

	public Site setExtraLanguages(String langStr) {
		String trimmed = langStr.trim();
		this.extraLanguages = trimmed;
		this.extraLanguagesArray = StringUtils.isNotBlank(trimmed) ? trimmed.split("[, ]") : new String[0];
		return this;
	}
	
	public String[] getExtraLanguagesArray() {
		return extraLanguagesArray;
	}

	public Site setExtraLanguagesArray(String[] extraLanguagesArray) {
		this.extraLanguagesArray = extraLanguagesArray;
		return this;
	}

	public boolean isMultilingual() {
		return this.extraLanguagesArray != null && this.extraLanguagesArray.length > 0;
	}

	public String getShortname() {
		return shortname;
	}

	public Site setShortname(String shortname) {
		this.shortname = shortname;
		return this;
	}
	
	public Host getEditorialHost() {
		return getHost(HostType.valueOf("editorial"));
	}
	
	public Host getDeliveryHost() {
		return getHost(HostType.valueOf("delivery"));
	}
	
	private Host getHost(HostType type) {
		return getCmsService().getHostService().getHost(getId(), type);
	}

	public boolean isSecured() {
		return secured;
	}

	public Site setSecured(boolean secured) {
		this.secured = secured;
		return this;
	}
	
	public List<User> getContributors() {
		return getCmsService().getSiteService().getContributors(getId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((extraLanguages == null) ? 0 : extraLanguages.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (secured ? 1231 : 1237);
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
		if (extraLanguages == null) {
			if (other.extraLanguages != null)
				return false;
		} else if (!extraLanguages.equals(other.extraLanguages))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (secured != other.secured)
			return false;
		if (shortname == null) {
			if (other.shortname != null)
				return false;
		} else if (!shortname.equals(other.shortname))
			return false;
		return true;
	}

}
