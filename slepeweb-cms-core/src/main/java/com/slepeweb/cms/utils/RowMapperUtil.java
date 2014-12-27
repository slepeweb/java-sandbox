package com.slepeweb.cms.utils;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.LoggerBean;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SiteConfig;
import com.slepeweb.cms.bean.Template;

public class RowMapperUtil {

	public static final class SiteMapper implements RowMapper<Site> {
		public Site mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeSite().
					setId(rs.getLong("id")).
					setName(rs.getString("name")).
					setHostname(rs.getString("hostname")).
					setShortname(rs.getString("shortname"));
		}
	}
	
	public static final class ItemTypeMapper implements RowMapper<ItemType> {
		public ItemType mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeItemType().
					setId(rs.getLong("id")).
					setName(rs.getString("name")).
					setMimeType(rs.getString("mimetype"));
		}
	}
	
	public static final class ItemMapper implements RowMapper<Item> {
		public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
			return mapItem(rs);
		}
	}
	
	private static Item mapItem(ResultSet rs) throws SQLException {
		Item item = CmsBeanFactory.makeItem().
				setId(rs.getLong("id")).
				setName(rs.getString("name")).
				setSimpleName(rs.getString("simplename")).
				setPath(rs.getString("path")).
				setDateCreated(rs.getTimestamp("datecreated")).
				setDateUpdated(rs.getTimestamp("dateupdated")).
				setDeleted(rs.getBoolean("deleted")).
				setPublished(rs.getBoolean("published"));
		
		ItemType type = CmsBeanFactory.makeItemType().
				setId(rs.getLong("typeid")).
				setName(rs.getString("typename")).
				setMimeType(rs.getString("mimetype"));
		
		item.setType(type);
		
		Site site = CmsBeanFactory.makeSite().
				setId(rs.getLong("siteid")).
				setName(rs.getString("sitename")).
				setHostname(rs.getString("hostname")).
				setShortname(rs.getString("site_shortname"));
		
		item.setSite(site);
		
		Template t = null;
		long templateId = rs.getLong("templateid");
		if (templateId != 0) {
			t = CmsBeanFactory.makeTemplate().
					setId(templateId).
					setName(rs.getString("templatename")).
					setForward(rs.getString("forward")).
					setItemTypeId(rs.getLong("typeid")).
					setSiteId(rs.getLong("siteid"));
		}
		
		item.setTemplate(t);
		return item;
	}
	
	public static final class LinkMapper implements RowMapper<Link> {
		public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeLink().
					setParentId(rs.getLong("parentid")).
					setChild(mapItem(rs)).
					setType(rs.getString("linktype")).
					setName(rs.getString("linkname")).
					setOrdering(rs.getInt("ordering"));
		}
	}
	
	public static final class ParentLinkMapper implements RowMapper<Link> {
		public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeLink().
					setParentId(rs.getLong("childid")).
					setChild(mapItem(rs)).
					setType(rs.getString("linktype")).
					setName(rs.getString("linkname")).
					setOrdering(rs.getInt("ordering"));
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
					setName(rs.getString("name"));
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
				setHelp(rs.getString("helptext")).
				setType(FieldType.valueOf(rs.getString("fieldtype"))).
				setSize(rs.getInt("size")).
				setDefaultValue(rs.getString("dflt")).
				setValidValues(rs.getString("valid"));
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
					setDateValue(rs.getTimestamp("datevalue"));
		}
	}
	
	public static final class MediaMapper implements RowMapper<Blob> {
		public Blob mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getBlob("data");
		}
	}
	
	public static final class TemplateMapper implements RowMapper<Template> {
		public Template mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeTemplate().
					setId(rs.getLong("id")).
					setName(rs.getString("name")).
					setForward(rs.getString("forward")).
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
	
}
