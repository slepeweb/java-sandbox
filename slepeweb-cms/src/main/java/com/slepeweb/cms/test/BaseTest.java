package com.slepeweb.cms.test;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.service.CmsService;

public abstract class BaseTest {
	protected static final String TEST_SITE_NAME = "Integration Testing";
	protected static final String HOMEPAGE_TYPE_NAME = TEST_SITE_NAME + " Homepage";
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
	protected static final String NEWS_TEMPLATE_NAME = "News Template";
	
	@Autowired protected CmsService cmsService;
	
	protected abstract TestResultSet execute();
	
	protected Site getTestSite() {
		return this.cmsService.getSiteService().getSite(TEST_SITE_NAME);
	}

	protected Site addSite(String name, String hostname, String homepageTypeName) {
		return CmsBeanFactory.makeSite().setName(name).setHostname(hostname).save();	
	}
	
	protected ItemType addType(String name) {
		return addType(name, "application/cms");
	}
	
	protected ItemType addType(String name, String mimeType) {
		ItemType it = CmsBeanFactory.makeItemType().setName(name).setMimeType(mimeType);
		it.save();
		return it;
	}

	protected Field addField(String name, String variable, String help, FieldType type, int size, Object dflt) {
		Field f = CmsBeanFactory.makeField().setName(name).setVariable(variable).setHelp(help).setType(type).
				setSize(size).setDefaultValue(dflt);
		f.save();
		return f;
	}
	
	protected Item addItem(Item parent, String name, String simplename, 
			Timestamp dateCreated, Timestamp dateUpdated, Site site, ItemType type, Template t) {
		
		String path = 
			parent != null ? 
				parent.isSiteRoot() ? parent.getPath() + simplename : parent.getPath() + "/" + simplename : 
					"/" + simplename;
		
		Item i = CmsBeanFactory.makeItem().setName(name).setSimpleName(simplename).setPath(path).
			setDateCreated(dateCreated).setDateUpdated(dateUpdated).setSite(site).setType(type).setTemplate(t);
		
		return parent != null ? parent.addChild(i) : null;
	}
	
	protected Template addTemplate(String name, String forward, Long siteId, Long typeId) {
		return CmsBeanFactory.makeTemplate().setName(name).setSiteId(siteId).setItemTypeId(typeId).save();
	}
}
