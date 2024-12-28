package com.slepeweb.cms.bean;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.commerce.bean.Axis;
import com.slepeweb.commerce.bean.AxisValue;
import com.slepeweb.commerce.bean.OrderItem;
import com.slepeweb.commerce.bean.Product;
import com.slepeweb.commerce.bean.Variant;

@Component
public class CmsBeanFactory {
	
	private static CmsService CMS;
	
	public static void init(CmsService s) {
		CMS = s;
	}
	
	public static Host makeHost() {
		Host h = new Host();
		h.setCmsService(CMS);
		return h;
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
	
	public static Item makeItem(String itemTypeName, String language) {
		Item i = null;
		
		if (
				CMS.isCommerceEnabled() && 
				itemTypeName != null && 
				itemTypeName.equals(ItemTypeName.PRODUCT)) {
			
			i = makeProduct();
		}
		else if (itemTypeName != null && itemTypeName.equals(ItemTypeName.SHORTCUT)) {
			i = makeShortcut();
		}
		else {
			i = new Item();
			i.setCmsService(CMS);
		}
		
		i.setLanguage(language);
		return i;
	}
	
	public static Item makeItem(String itemTypeName) {
		return makeItem(itemTypeName, "en");
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
	
	public static LinkType makeLinkType() {
		LinkType lt = new LinkType();
		lt.setCmsService(CMS);
		return lt;
	}
	
	public static LinkName makeLinkName() {
		LinkName ln = new LinkName();
		ln.setCmsService(CMS);
		return ln;
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
	
	public static SiteConfig makeSiteConfig() {
		SiteConfig sc = new SiteConfig();
		sc.setCmsService(CMS);
		return sc;
	}
	
	public static Media makeMedia() {
		Media m = new Media();
		m.setCmsService(CMS);
		return m;
	}
	
	public static Tag makeTag() {
		Tag tag = new Tag();
		tag.setCmsService(CMS);
		return tag;
	}
	
	public static Item makeHomepageItem(Site s) throws ResourceException {
		ItemType homepageType = makeHomepageType(s.getName());
		return proveItem(s, homepageType, "Homepage", "", "/").save();
	}
	
	public static Item makeContentFolderRootItem(Site s, ItemType it) throws ResourceException {
		return proveItem(s, it, "Content", "content", Item.CONTENT_ROOT_PATH).save();
	}
	
	public static ItemType makeContentFolderType() {
		return makeItemType().
				setName(ItemType.CONTENT_FOLDER_TYPE_NAME).
				save();
	}
	
	public static ItemType makeHomepageType(String siteName) {
		return makeItemType().setName(siteName + " Homepage").save();
	}
	
	private static Item proveItem(Site s, ItemType type, String name, String simpleName, String path) {
		Item i = makeItem(type.getName()).
			setName(name).
			setSimpleNameOnly(simpleName).
			setPath(path).
			setSite(s).
			setType(type).
			setDateCreated(new Timestamp(System.currentTimeMillis()));
			
		return i.
			setDateUpdated(i.getDateCreated()).
			setDeleted(false);
	}
	
	public static Shortcut makeShortcut() {
		Shortcut sh = new Shortcut();
		sh.setCmsService(CMS);
		
		// A shortcut item must always be published in order for it to be visible as
		// a child link.
		// (see LinkServiceImpl.getLinks())
		sh.setPublished(true);
		return sh;
	}
	
	public static Product makeProduct() {
		Product p = new Product();
		p.setCmsService(CMS);
		return p;
	}
	
	public static Axis makeAxis() {
		Axis a = new Axis();
		a.setCmsService(CMS);
		return a;
	}
	
	public static AxisValue makeAxisValue() {
		AxisValue av = new AxisValue();
		av.setCmsService(CMS);
		return av;
	}
	
	public static Variant makeVariant() {
		Variant v = new Variant();
		v.setCmsService(CMS);
		return v;
	}
	
	public static OrderItem makeOrderItem(int n, long o, String q) {
		OrderItem oi = new OrderItem(n, o, q);
		oi.setCmsService(CMS);
		return oi;
	}
	
	public static AccessRule makeAccessRule() {
		AccessRule ar = new AccessRule();
		ar.setCmsService(CMS);
		return ar;
	}
	
	public static User makeUser() {
		User u = new User();
		u.setCmsService(CMS);
		return u;
	}
	
	public static Link toChildLink(Item parent, Item child, String linkType) {
		return CmsBeanFactory.makeLink().
				setParentId(parent.getId()).
				setChild(child).
				setType(linkType).
				setName("std").
				setOrdering(0); // Arbitrary value
	}
	
	public static SiteType makeSiteType() {
		SiteType st = new SiteType();
		st.setCmsService(CMS);
		return st;
	}
	
}
