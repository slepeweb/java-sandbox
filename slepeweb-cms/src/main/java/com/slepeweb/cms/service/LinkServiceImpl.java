package com.slepeweb.cms.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class LinkServiceImpl extends BaseServiceImpl implements LinkService {
	
	private static Logger LOG = Logger.getLogger(LinkServiceImpl.class);
	
	public void insertLink(Link l) {
		if (l.isDefined4Insert()) {
			this.jdbcTemplate.update(
					"insert into link (parentid, childid, linktype, name, ordering) values (?, ?, ?, ?, ?)", 
					l.getParent().getId(), l.getChild().getId(), l.getType().name(), l.getName(), l.getOrdering());
			
			LogUtil.info(LOG, "Added new link", "");
		}
	}

	public void deleteLinks(Long parentId, Long childId) {
		if (this.jdbcTemplate.update("delete from link where parentid = ? and childid = ?", parentId, childId) > 0) {
			LogUtil.warn(LOG, "Deleted links", String.valueOf(parentId) + " -> " + String.valueOf(childId));
		}
	}

	public void deleteLinks(Long parentId, String linkType, String name) {
		String sql = "delete from link where parentid = ? and linktype = ? ";
		if (StringUtils.isNotBlank(name)) {
			sql += "and name = ?";
			
			if (this.jdbcTemplate.update(sql, parentId, linkType, name) > 0) {
				LogUtil.warn(LOG, "Deleted links", String.valueOf(parentId) + " -> " + String.valueOf(linkType));
			}
		}
		else {
			if (this.jdbcTemplate.update(sql, parentId, linkType) > 0) {
				LogUtil.warn(LOG, "Deleted links", String.valueOf(parentId) + " -> " + String.valueOf(linkType));
			}
		}		
	}


	/**
	 * parent item must be fully defined.
	 */
	public List<Link> getLinks(Item parent, String linkType, String name) {
		Object[] params;
		String sql = 
			"select i.*, s.name as sitename, s.hostname, it.name as typename, l.linktype, l.name, l.ordering from " +
			"item i, site s, itemtype it, link l where l.childid = i.id and " +
			"i.siteid=s.id and i.typeid=it.id and l.parentid = ? and l.linktype = ? and i.deleted=0 ";
		
		 if (StringUtils.isNotBlank(name)) {
			 sql += "and name = ? ";
			 params = new Object[] {parent.getId(), linkType, name};
		 }
		 else {
			 params = new Object[] {parent.getId(), linkType};
		 }
		 
		 sql += "order by l.ordering";
		 List<Link> result = this.jdbcTemplate.query(sql, params, new RowMapperUtil.LinkMapper());
		 
		 // Loop through results to set parent item
		 for (Link l : result) {
			 l.setParent(parent);
		 }
		 
		 return result;
	}
}
