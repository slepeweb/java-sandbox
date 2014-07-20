package com.slepeweb.cms.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.Link.LinkType;
import com.slepeweb.cms.bean.Site;

public class RowMapperUtil {

	public static final class SiteMapper implements RowMapper<Site> {
		public Site mapRow(ResultSet rs, int rowNum) throws SQLException {
			Site s = new Site();
			s.setId(rs.getLong("id"));
			s.setName(rs.getString("name"));
			s.setHostname(rs.getString("hostname"));
			return s;
		}
	}
	
	public static final class ItemTypeMapper implements RowMapper<ItemType> {
		public ItemType mapRow(ResultSet rs, int rowNum) throws SQLException {
			ItemType it = new ItemType();
			it.setId(rs.getLong("id"));
			it.setName(rs.getString("name"));
			return it;
		}
	}
	
	public static final class ItemMapper implements RowMapper<Item> {
		public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
			return mapItem(rs);
		}
	}
	
	private static Item mapItem(ResultSet rs) throws SQLException {
		Item item = new Item();
		item.setId(rs.getLong("id"));
		item.setName(rs.getString("name"));
		item.setPath(rs.getString("path"));
		item.setDateCreated(rs.getTimestamp("datecreated"));
		item.setDateUpdated(rs.getTimestamp("dateupdated"));
		item.setDeleted(rs.getBoolean("deleted"));
		
		ItemType type = new ItemType();
		item.setType(type);
		type.setId(rs.getLong("typeid"));
		type.setName(rs.getString("typename"));
		
		Site site = new Site();
		item.setSite(site);
		site.setId(rs.getLong("siteid"));
		site.setName(rs.getString("sitename"));
		site.setHostname(rs.getString("hostname"));
		return item;
	}
	
	public static final class LinkMapper implements RowMapper<Link> {
		public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
			Link it = new Link();
			it.setChild(mapItem(rs));
			it.setType(LinkType.valueOf(rs.getString("linktype")));
			it.setName(rs.getString("name"));
			it.setOrdering(rs.getInt("ordering"));
			return it;
		}
	}
	
	public static final class FieldMapper implements RowMapper<Field> {
		public Field mapRow(ResultSet rs, int rowNum) throws SQLException {
			Field f = new Field();
			f.setId(rs.getLong("id"));
			f.setName(rs.getString("name"));
			f.setVariable(rs.getString("variable"));
			f.setHelp(rs.getString("helptext"));
			f.setType(FieldType.valueOf(rs.getString("fieldtype")));
			f.setSize(rs.getInt("size"));
			return f;
		}
	}
	
}
