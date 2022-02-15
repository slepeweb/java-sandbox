package com.slepeweb.cms.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.slepeweb.cms.bean.AccessRule;
import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Host.HostType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.LoggerBean;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.Shortcut;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SiteConfig;
import com.slepeweb.cms.bean.SiteType;
import com.slepeweb.cms.bean.Tag;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.bean.User;

public class RowMapperUtil {

	public static final class HostMapper implements RowMapper<Host> {
		public Host mapRow(ResultSet rs, int rowNum) throws SQLException {
			Host h = CmsBeanFactory.makeHost().
					setId(rs.getLong("id")).
					setName(rs.getString("name")).
					setPort(rs.getInt("port")).
					setType(HostType.valueOf(rs.getString("type"))).
					setProtocol(rs.getString("protocol"));
			
			Site s = mapSite(rs, "siteid", "sitename", "shortname");
			
			return h.setSite(s);			
		}
	}
	
	public static final class SiteMapper implements RowMapper<Site> {
		public Site mapRow(ResultSet rs, int rowNum) throws SQLException {
			return mapSite(rs, "id", "name", "shortname");
		}
	}
	
	private static Site mapSite(ResultSet rs, String id, String name, String shortname) 
			throws SQLException {
		
		return CmsBeanFactory.makeSite().
				setId(rs.getLong(id)).
				setName(rs.getString(name)).
				setShortname(rs.getString(shortname)).
				setLanguage(rs.getString("language")).
				setExtraLanguages(rs.getString("xlanguages")).
				setSecured(rs.getBoolean("secured"));
	}
	
	public static final class ItemTypeMapper implements RowMapper<ItemType> {
		public ItemType mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeItemType().
					setId(rs.getLong("id")).
					setName(rs.getString("name")).
					setMimeType(rs.getString("mimetype")).
					setPrivateCache(rs.getLong("privatecache")).
					setPublicCache(rs.getLong("publiccache"));
		}
	}
	
	public static final class ItemMapper implements RowMapper<Item> {
		public Item mapRow(ResultSet rs, int rowNum) throws SQLException {			
			return mapItem(rs);
		}
	}
	
	private static Item mapItem(ResultSet rs) throws SQLException {		

		String itemTypeName = rs.getString("typename");
		/*
		 *  CmsBeanFactory.makeItem() will make either an Item or a Shortcut or a Product,
		 *  depending on the item type name.
		 */
		Item item = CmsBeanFactory.makeItem(itemTypeName).
				setId(rs.getLong("id")).
				setOrigId(rs.getLong("origid")).
				setName(rs.getString("name")).
				setSimpleName(rs.getString("simplename")).
				setPath(rs.getString("path")).
				setDateCreated(rs.getTimestamp("datecreated")).
				setDateUpdated(rs.getTimestamp("dateupdated")).
				setDeleted(rs.getBoolean("deleted")).
				setEditable(rs.getBoolean("editable")).
				setPublished(rs.getBoolean("published")).
				setSearchable(rs.getBoolean("searchable")).
				setVersion(rs.getInt("version"));
		
		item.setType(mapItemType(rs));
		
		Site site = mapSite(rs, "siteid", "sitename", "site_shortname");		
		item.setSite(site);
		
		if (! item.isShortcut()) {
			Template t = null;
			long templateId = rs.getLong("templateid");
			if (templateId != 0) {
				t = CmsBeanFactory.makeTemplate().
						setId(templateId).
						setName(rs.getString("templatename")).
						setController(rs.getString("forward")).
						setItemTypeId(rs.getLong("typeid")).
						setSiteId(rs.getLong("siteid"));
			}
			
			item.setTemplate(t);
		}
		
		identifyTargetIfShortcut(item);
		return item;
	}
	
	private static ItemType mapItemType(ResultSet rs) throws SQLException {	
		return CmsBeanFactory.makeItemType().
			setId(rs.getLong("typeid")).
			setName(rs.getString("typename")).
			setMimeType(rs.getString("mimetype")).
			setPrivateCache(rs.getLong("privatecache")).
			setPublicCache(rs.getLong("publiccache"));
	}
	
	private static void identifyTargetIfShortcut(Item i) {
		if (i.isShortcut()) {
			List<Link> links = i.getCmsService().getLinkService().getLinks(i.getId(), LinkType.shortcut);
			if (links.size() > 0) {
				Shortcut sh = (Shortcut) i;
				sh.setReferred(links.get(0).getChild());
			}
		}
	}
	
	public static final class LinkMapper implements RowMapper<Link> {
		public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
			Link l = CmsBeanFactory.makeLink().
					setParentId(rs.getLong("parentid")).
					setChild(mapItem(rs)).
					setType(rs.getString("linktype")).
					setName(rs.getString("linkname")).
					setOrdering(rs.getInt("ordering")).
					setData(rs.getString("data"));
			
			// This will already have been executed in the call to mapItem(rs)
			// identifyTargetIfShortcut(l.getChild());
			
			return l;
		}
	}
	
	public static final class ParentLinkMapper implements RowMapper<Link> {
		public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeLink().
					setParentId(rs.getLong("childid")).
					setChild(mapItem(rs)).
					setType(rs.getString("linktype")).
					setName(rs.getString("linkname")).
					setOrdering(rs.getInt("ordering")).
					setData(rs.getString("data"));
		}
	}
	
	public static final class LinkTypeMapper implements RowMapper<LinkType> {
		public LinkType mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeLinkType().
					setId(rs.getLong("id")).
					setName(rs.getString("name"));
		}
	}
	
	public static final class LinkNameMapper implements RowMapper<LinkName> {
		public LinkName mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeLinkName().
					setId(rs.getLong("id")).
					setSiteId(rs.getLong("siteid")).
					setLinkTypeId(rs.getLong("linktypeid")).
					setName(rs.getString("name")).
					setGuidance(rs.getString("guidance"));
		}
	}
	
	public static final class FieldMapper implements RowMapper<Field> {
		public Field mapRow(ResultSet rs, int rowNum) throws SQLException {
			return mapField(rs);
		}
	}
	
	public static Field mapField(ResultSet rs) throws SQLException {
		return CmsBeanFactory.makeField().
				setId(rs.getLong("id")).
				setName(rs.getString("name")).
				setVariable(rs.getString("variable")).
				setMultilingual(rs.getBoolean("multilingual")).
				setHelp(rs.getString("helptext")).
				setType(FieldType.valueOf(rs.getString("fieldtype"))).
				setSize(rs.getInt("size")).
				setDefaultValue(rs.getString("dflt")).
				setValidValues(rs.getString("valid")).
				setValidationRegExp(rs.getString("validation"));
	}
	
	@SuppressWarnings("unused")
	private static boolean setFieldId(ResultSet rs, String name, Field f) {
		try {
			f.setId(rs.getLong(name));
			return true;
		}
		catch (SQLException e) {}
		return false;
	}
	
	public static final class FieldForTypeMapper implements RowMapper<FieldForType> {
		public FieldForType mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeFieldForType().
					setField(mapField(rs)).
					setTypeId(rs.getLong("itemtypeid")).
					setOrdering(rs.getLong("fieldorder")).
					setMandatory(rs.getBoolean("mandatory"));
		}
	}
	
	public static final class FieldValueMapper implements RowMapper<FieldValue> {
		public FieldValue mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeFieldValue().
					setField(mapField(rs)).
					setItemId(rs.getLong("itemid")).
					setStringValue(rs.getString("stringvalue")).
					setIntegerValue(rs.getInt("integervalue")).
					setDateValue(rs.getTimestamp("datevalue")).
					setLanguage(rs.getString("language"));
		}
	}
	
	public static final class MediaMapper implements RowMapper<Media> {
		public Media mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeMedia().
					setItemId(rs.getLong("itemid")).
					setSize(rs.getLong("size")).
					setBlob(rs.getBlob("data")).
					setFolder(rs.getString("folder")).
					setThumbnail(rs.getBoolean("thumbnail"));
		}
	}
	
	public static final class MediaSizeMapper implements RowMapper<Long> {
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getLong("size");
		}
	}
	
	public static final class TemplateMapper implements RowMapper<Template> {
		public Template mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeTemplate().
					setId(rs.getLong("id")).
					setName(rs.getString("name")).
					setController(rs.getString("forward")).
					setSiteId(rs.getLong("siteid")).
					setItemTypeId(rs.getLong("typeid"));
		}
	}
	
	public static final class SiteConfigMapper implements RowMapper<SiteConfig> {
		public SiteConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeSiteConfig().
					setSiteId(rs.getLong("siteid")).
					setName(rs.getString("name")).
					setValue(rs.getString("value"));
		}
	}
	
	public static final class LoglevelMapper implements RowMapper<LoggerBean> {
		public LoggerBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new LoggerBean().
					setPackag(rs.getString("package")).
					setLevel(rs.getString("level"));
		}
	}
	
	public static final class TagMapper implements RowMapper<Tag> {
		public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeTag().
					setSiteId(rs.getLong("siteid")).
					setItemId(rs.getLong("itemid")).
					setValue(rs.getString("value"));
		}
	}
	
	public static final class TagValueMapper implements RowMapper<String> {
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString("value");
		}
	}
	
	public static final class AccessMapper implements RowMapper<AccessRule> {
		public AccessRule mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeAccessRule().
					setId(rs.getLong("id")).
					setSiteShortname(rs.getString("site")).
					setName(rs.getString("name")).
					setRolePattern(rs.getString("role")).
					setItemTypePattern(rs.getString("itemtype")).
					setTemplatePattern(rs.getString("template")).
					setItemPathPattern(rs.getString("path")).
					setAccess(rs.getBoolean("access")).
					setEnabled(rs.getBoolean("enabled"));
		}
	}
	
	public static final class UserMapper implements RowMapper<User> {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeUser().
					setId(rs.getLong("id")).
					setFirstName(rs.getString("firstname")).
					setLastName(rs.getString("lastname")).
					setEmail(rs.getString("email")).
					setPhone(rs.getString("phone")).
					setPassword(rs.getString("password")).
					setEnabled(rs.getBoolean("enabled")).
					setSecret(rs.getString("secret"));
		}
	}
	
	public static final class RoleMapper implements RowMapper<String> {
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString("role");
		}
	}
	
	public static final class SiteTypeMapper implements RowMapper<SiteType> {
		public SiteType mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeSiteType().
					setSiteId(rs.getLong("siteid")).
					setType(mapItemType(rs));
		}
	}
	
}
