package com.slepeweb.cms.test;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.service.CmsService;

public abstract class BaseTest {
	protected static final String TEST_SITE_NAME = "Integration Testing";
	protected static final String HOMEPAGE_TYPE_NAME = "ZHomepage";
	protected static final String SECTION_TYPE_NAME = "ZSection";
	protected static final String NEWS_TYPE_NAME = "ZNews";
	protected static final String EVENT_TYPE_NAME = "ZEvent";
	protected static final String ARTICLE_TYPE_NAME = "ZArticle";
	protected static final String IMAGE_TYPE_NAME = "ZImage";
	protected static final String TITLE_FIELD_NAME = "ztitle";
	protected static final String TEASER_FIELD_NAME = "zteaser";
	protected static final String BODY_FIELD_NAME = "zbodytext";
	protected static final String EMBARGO_FIELD_NAME = "zembargodate";
	protected static final String ALTTEXT_FIELD_NAME = "zalttext";
	
	@Autowired protected CmsService cmsService;
	
	protected abstract TestResultSet execute();
	
	protected Site getTestSite() {
		return this.cmsService.getSiteService().getSite(TEST_SITE_NAME);
	}

	protected Site addSite(String name, String hostname, String homepageTypeName) {
		Site s = CmsBeanFactory.getSite().setName(name).setHostname(hostname);	

		
		String rootName = "Homepage";
		ItemType type = this.cmsService.getItemTypeService().getItemType(homepageTypeName);
		Item homepageItem = null;
		
		if (type != null) {
			homepageItem = CmsBeanFactory.getItem().
				setName(rootName).
				setSimpleName("").
				setPath("/").
				setSite(s).
				setType(type).
				setDateCreated(new Timestamp(System.currentTimeMillis()));
			
			homepageItem.
				setDateUpdated(homepageItem.getDateCreated()).
				setDeleted(false);
		}
		
		s.save(homepageItem);
		return s;
	}
	
	protected ItemType addType(String name) {
		return addType(name, false);
	}
	
	protected ItemType addType(String name, boolean isMedia) {
		ItemType it = CmsBeanFactory.getItemType().setName(name).setMedia(isMedia);
		it.save();
		return it;
	}

	protected Field addField(String name, String variable, String help, FieldType type, int size, Object dflt) {
		Field f = CmsBeanFactory.getField().setName(name).setVariable(variable).setHelp(help).setType(type).
				setSize(size).setDefaultValue(dflt);
		f.save();
		return f;
	}
	
	protected Item addItem(Item parent, String name, String simplename, 
			Timestamp dateCreated, Timestamp dateUpdated, Site site, ItemType type) {
		
		String path = parent.isRoot() ? parent.getPath() + simplename : parent.getPath() + "/" + simplename;
		Item i = CmsBeanFactory.getItem().setName(name).setSimpleName(simplename).setPath(path).
			setDateCreated(dateCreated).setDateUpdated(dateUpdated).setSite(site).setType(type);
		
		return parent.addChild(i);
	}
	
}
