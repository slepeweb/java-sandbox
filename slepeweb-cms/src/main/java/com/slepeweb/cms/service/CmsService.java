package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;

public interface CmsService {
	SiteService getSiteService();
	ItemTypeService getItemTypeService();
	FieldService getFieldService();
	ItemService getItemService();
	LinkService getLinkService();
	FieldForTypeService getFieldForTypeService();
	
	void addSite(Site s);
	Site getSite(String name);
	Site getSite(Long id);
	List<Site> getAllSites();
	Item getItem(Long id);
	ItemType getItemType(String name);
	void addItemType(ItemType it);
	Field getField(String name);
	void addField(Field f);
}
