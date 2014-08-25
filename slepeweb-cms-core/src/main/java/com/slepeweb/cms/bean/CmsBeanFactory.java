package com.slepeweb.cms.bean;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import com.slepeweb.cms.service.CmsService;

@Component
public class CmsBeanFactory {
	
	private static CmsService CMS;
	
	public static void init(CmsService s) {
		CMS = s;
	}
	
	public static Site makeSite() {
		Site s = new Site();
		s.setCmsService(CMS);
		return s;
	}
	
	public static ItemType makeItemType() {
		ItemType it = new ItemType();
		it.setCmsService(CMS);
		return it;
	}
	
	public static Field makeField() {
		Field f = new Field();
		f.setCmsService(CMS);
		return f;
	}
	
	public static Item makeItem() {
		Item i = new Item();
		i.setCmsService(CMS);
		return i;
	}
	
	public static Template makeTemplate() {
		Template t = new Template();
		t.setCmsService(CMS);
		return t;
	}
	
	public static Link makeLink() {
		Link l = new Link();
		l.setCmsService(CMS);
		return l;
	}
	
	public static FieldForType makeFieldForType() {
		FieldForType fft = new FieldForType();
		fft.setCmsService(CMS);
		return fft;
	}
	
	public static FieldValue makeFieldValue() {
		FieldValue fv = new FieldValue();
		fv.setCmsService(CMS);
		return fv;
	}
	
	public static Item makeHomepageItem(Site s) {
		ItemType homepageType = makeHomepageType(s.getName());
		return proveItem(s, homepageType, "Homepage", "", "/").save();
	}
	
	public static Item makeContentFolderRootItem(Site s, ItemType it) {
		return proveItem(s, it, "Content", "content", "/content").save();
	}
	
	public static ItemType makeContentFolderType() {
		return makeItemType().setName(ItemType.CONTENT_FOLDER_TYPE_NAME).save();
	}
	
	public static ItemType makeHomepageType(String siteName) {
		return makeItemType().setName(siteName + " Homepage").save();
	}
	
	private static Item proveItem(Site s, ItemType type, String name, String simpleName, String path) {
		Item i = makeItem().
			setName(name).
			setSimpleName(simpleName).
			setPath(path).
			setSite(s).
			setType(type).
			setDateCreated(new Timestamp(System.currentTimeMillis()));
			
		return i.
			setDateUpdated(i.getDateCreated()).
			setDeleted(false);
	}
}
