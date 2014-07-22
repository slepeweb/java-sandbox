package com.slepeweb.cms.bean;

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
}
