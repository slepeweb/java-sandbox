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
import com.slepeweb.cms.bean.Link.LinkType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Template;

public class RowMapperUtil {

	public static final class SiteMapper implements RowMapper<Site> {
		public Site mapRow(ResultSet rs, int rowNum) throws SQLException {
			Site s = CmsBeanFactory.getSite();
			s.setId(rs.getLong("id"));
			s.setName(rs.getString("name"));
			s.setHostname(rs.getString("hostname"));
			return s;
		}
	}
	
	public static final class ItemTypeMapper implements RowMapper<ItemType> {
		public ItemType mapRow(ResultSet rs, int rowNum) throws SQLException {
			ItemType it = CmsBeanFactory.getItemType();
			it.setId(rs.getLong("id"));
			it.setName(rs.getString("name"));
			it.setMedia(rs.getBoolean("media"));
			return it;
		}
	}
	
	public static final class ItemMapper implements RowMapper<Item> {
		public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
			return mapItem(rs);
		}
	}
	
	private static Item mapItem(ResultSet rs) throws SQLException {
		Item item = CmsBeanFactory.getItem().
				setId(rs.getLong("id")).
				setName(rs.getString("name")).
				setSimpleName(rs.getString("simplename")).
				setPath(rs.getString("path")).
				setDateCreated(rs.getTimestamp("datecreated")).
				setDateUpdated(rs.getTimestamp("dateupdated")).
				setDeleted(rs.getBoolean("deleted"));
		
		ItemType type = CmsBeanFactory.getItemType().
				setId(rs.getLong("typeid")).
				setName(rs.getString("typename")).
				setMedia(rs.getBoolean("media"));
		
		item.setType(type);
		
		Site site = CmsBeanFactory.getSite().
				setId(rs.getLong("siteid")).
				setName(rs.getString("sitename")).
				setHostname(rs.getString("hostname"));
		
		item.setSite(site);
		
		Template t = null;
		long templateId = rs.getLong("templateid");
		if (templateId != 0) {
			t = CmsBeanFactory.getTemplate().
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
			Link l = CmsBeanFactory.getLink();
			l.setParentId(rs.getLong("parentid"));
			l.setChild(mapItem(rs));
			l.setType(LinkType.valueOf(rs.getString("linktype")));
			l.setName(rs.getString("linkname"));
			l.setOrdering(rs.getInt("ordering"));
			return l;
		}
	}
	
	public static final class FieldMapper implements RowMapper<Field> {
		public Field mapRow(ResultSet rs, int rowNum) throws SQLException {
			return mapField(rs);
		}
	}
	
	public static Field mapField(ResultSet rs) throws SQLException {
		Field f = CmsBeanFactory.getField();
		f.setId(rs.getLong("id"));
		f.setName(rs.getString("name"));
		f.setVariable(rs.getString("variable"));
		f.setHelp(rs.getString("helptext"));
		f.setType(FieldType.valueOf(rs.getString("fieldtype")));
		f.setSize(rs.getInt("size"));
		f.setDefaultValue(rs.getString("dflt"));
		return f;
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
			FieldForType fft = CmsBeanFactory.getFieldForType();
			fft.setField(mapField(rs));
			fft.setTypeId(rs.getLong("itemtypeid"));
			fft.setOrdering(rs.getLong("fieldorder"));
			fft.setMandatory(rs.getBoolean("mandatory"));
			return fft;
		}
	}
	
	public static final class FieldValueMapper implements RowMapper<FieldValue> {
		public FieldValue mapRow(ResultSet rs, int rowNum) throws SQLException {
			FieldValue fv = CmsBeanFactory.getFieldValue();
			fv.setField(mapField(rs));
			fv.setItemId(rs.getLong("itemid"));
			fv.setStringValue(rs.getString("stringvalue"));
			fv.setIntegerValue(rs.getInt("integervalue"));
			fv.setDateValue(rs.getTimestamp("datevalue"));
			return fv;
		}
	}
	
	public static final class MediaMapper implements RowMapper<Blob> {
		public Blob mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getBlob("data");
		}
	}
	
	public static final class TemplateMapper implements RowMapper<Template> {
		public Template mapRow(ResultSet rs, int rowNum) throws SQLException {
			Template t = CmsBeanFactory.getTemplate().
					setId(rs.getLong("id")).
					setName(rs.getString("name")).
					setForward(rs.getString("forward")).
					setSiteId(rs.getLong("siteid")).
					setItemTypeId(rs.getLong("typeid"));
			return t;
		}
	}
	
}
