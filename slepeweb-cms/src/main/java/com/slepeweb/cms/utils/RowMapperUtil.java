package com.slepeweb.cms.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
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
	}
}
