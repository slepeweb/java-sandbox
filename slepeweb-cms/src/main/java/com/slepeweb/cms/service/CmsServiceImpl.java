package com.slepeweb.cms.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.CmsBeanFactory;

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
