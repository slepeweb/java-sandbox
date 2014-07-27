package com.slepeweb.cms.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;

@Service
public class CmsServiceImpl extends BaseServiceImpl implements CmsService {
	
	@Autowired protected SiteService siteService;	
	@Autowired protected ItemTypeService itemTypeService;
	@Autowired protected FieldService fieldService;
	@Autowired protected ItemService itemService;
	@Autowired protected LinkService linkService;
	@Autowired protected FieldForTypeService fieldForTypeService;
	@Autowired protected FieldValueService fieldValueService;
	
	@PostConstruct
	public void initialiseCmsBeanFactory() {
		CmsBeanFactory.init(this);
	}
	
	public Site getSite(String name) {
		return this.siteService.getSite(name);
	}
	
	public Site getSite(Long id) {
		return this.siteService.getSite(id);
	}

	public void addSite(Site s) {
		this.siteService.save(s);
	}

	@Override
	public List<Site> getAllSites() {
		// TODO Auto-generated method stub
		return null;
	}

	public Item getItem(Long id) {
		return this.itemService.getItem(id);
	}

	public ItemType getItemType(String name) {
		return this.itemTypeService.getItemType(name);
	}

	public void addItemType(ItemType it) {
		this.itemTypeService.save(it);		
	}

	public Field getField(String name) {
		return this.fieldService.getField(name);
	}

	public void addField(Field f) {
		this.fieldService.save(f);
	}

	public SiteService getSiteService() {
		return siteService;
	}
	
	public ItemTypeService getItemTypeService() {
		return itemTypeService;
	}
	
	public FieldService getFieldService() {
		return fieldService;
	}
	
	public ItemService getItemService() {
		return itemService;
	}
	
	public LinkService getLinkService() {
		return linkService;
	}

	public FieldForTypeService getFieldForTypeService() {
		return fieldForTypeService;
	}

	public void setFieldForTypeService(FieldForTypeService fieldForTypeService) {
		this.fieldForTypeService = fieldForTypeService;
	}

	public FieldValueService getFieldValueService() {
		return fieldValueService;
	}

}
