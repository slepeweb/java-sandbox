package com.slepeweb.cms.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class LinkServiceImpl extends BaseServiceImpl implements LinkService {
	
	private static Logger LOG = Logger.getLogger(LinkServiceImpl.class);
	private static final String SELECTOR_TEMPLATE = 
			"select i.*, s.name as sitename, s.hostname, it.name as typename, it.media, l.parentid, l.linktype, l.name as linkname, l.ordering from "
			+ "item i, site s, itemtype it, link l where l.childid = i.id and i.siteid=s.id and i.typeid=it.id and "
			+ "%s and i.deleted=0 " + "order by l.linktype, l.name, l.ordering";

	public Link save(Link l) {
		if (l.isDefined4Insert()) {
			Link dbRecord = getLink(l.getParentId(), l.getChild().getId());		
			if (dbRecord != null) {
				updateLink(dbRecord, l);
			}
			else {
				insertLink(l);
			}
		}
		
		return l;
	}
	
	private void insertLink(Link l) {
		this.jdbcTemplate.update(
				"insert into link (parentid, childid, linktype, name, ordering) values (?, ?, ?, ?, ?)", 
				l.getParentId(), l.getChild().getId(), l.getType().name(), l.getName(), l.getOrdering());
		
		// Note: no new id generated for this bean
		LOG.info(compose("Added new link", l));
	}
	
	private void updateLink(Link dbRecord, Link l) {
		if (! dbRecord.equals(l)) {
			dbRecord.assimilate(l);
			
			this.jdbcTemplate.update(
					"update link set linktype = ?, name = ?, ordering = ? where parentid = ? and childid = ?", 
					dbRecord.getType().name(), dbRecord.getName(), dbRecord.getOrdering(), 
					dbRecord.getParentId(), dbRecord.getChild().getId());
			
			LOG.info(compose("Updated link link", l));
		}
		else {
			LOG.debug(compose("Link not modified", l));
		}
	}

	public void deleteLinks(Long parentId, Long childId) {
		if (this.jdbcTemplate.update("delete from link where parentid = ? and childid = ?", parentId, childId) > 0) {
			LOG.warn(compose("Deleted links", String.valueOf(parentId) + " -> " + String.valueOf(childId)));
		}
	}

	public void deleteLinks(Long parentId, String linkType, String name) {
		String sql = "delete from link where parentid = ? and linktype = ? ";
		if (StringUtils.isNotBlank(name)) {
			sql += "and name = ?";
			
			if (this.jdbcTemplate.update(sql, parentId, linkType, name) > 0) {
				LOG.warn(compose("Deleted links", String.valueOf(parentId) + " -> " + String.valueOf(linkType)));
			}
		}
		else {
			if (this.jdbcTemplate.update(sql, parentId, linkType) > 0) {
				LOG.warn(compose("Deleted links", String.valueOf(parentId) + " -> " + String.valueOf(linkType)));
			}
		}		
	}

	public List<Link> getLinks(Long parentId) {
		String sql = String.format(SELECTOR_TEMPLATE, "l.parentid = ?");
		return this.jdbcTemplate.query(sql, new Object[] {parentId}, new RowMapperUtil.LinkMapper());		 
	}

	public Link getLink(Long parentId, Long childId) {
		String sql = String.format(SELECTOR_TEMPLATE, "l.parentid = ? and l.childid = ?");
		return (Link) getFirstInList(this.jdbcTemplate.query(sql, new Object[] {parentId, childId}, 
				new RowMapperUtil.LinkMapper()));
	}
	
	public int getCount() {
		return getCount(null);
	}
	
	public int getCount(Long parentId) {
		if (parentId != null) {
			return this.jdbcTemplate.queryForInt("select count(*) from link where parentid = ?", parentId);
		}
		else {
			return this.jdbcTemplate.queryForInt("select count(*) from link");
		}
	}
	
	public Link getParent(Long childId) {
		String sql = String.format(SELECTOR_TEMPLATE, "l.childid = ? and l.linktype = 'binding'");
		return (Link) getFirstInList(this.jdbcTemplate.query(sql, new Object[] {childId}, 
				new RowMapperUtil.LinkMapper()));
	}
}
