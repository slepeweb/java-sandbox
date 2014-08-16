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
	
	public static Site getSite() {
		Site s = new Site();
		s.setCmsService(CMS);
		return s;
	}
	
	public static ItemType getItemType() {
		ItemType it = new ItemType();
		it.setCmsService(CMS);
		return it;
	}
	
	public static Field getField() {
		Field f = new Field();
		f.setCmsService(CMS);
		return f;
	}
	
	public static Item getItem() {
		Item i = new Item();
		i.setCmsService(CMS);
		return i;
	}
	
	public static Template getTemplate() {
		Template t = new Template();
		t.setCmsService(CMS);
		return t;
	}
	
	public static Link getLink() {
		Link l = new Link();
		l.setCmsService(CMS);
		return l;
	}
	
	public static FieldForType getFieldForType() {
		FieldForType fft = new FieldForType();
		fft.setCmsService(CMS);
		return fft;
	}
	
	public static FieldValue getFieldValue() {
		FieldValue fv = new FieldValue();
		fv.setCmsService(CMS);
		return fv;
	}
	
	public static Item bakeHomepageItem(Site s) {
		ItemType homepageType = bakeHomepageType(s.getName());
		return proveItem(s, homepageType, "Homepage", "", "/").save();
	}
	
	public static Item bakeContentFolderRootItem(Site s, ItemType it) {
		return proveItem(s, it, "Content", "content", "/content").save();
	}
	
	public static ItemType bakeContentFolderType() {
		return getItemType().setName(ItemType.CONTENT_FOLDER_TYPE_NAME).setMedia(false).save();
	}
	
	public static ItemType bakeHomepageType(String siteName) {
		return getItemType().setName(siteName + " Homepage").setMedia(false).save();
	}
	
	private static Item proveItem(Site s, ItemType type, String name, String simpleName, String path) {
		Item i = getItem().
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
