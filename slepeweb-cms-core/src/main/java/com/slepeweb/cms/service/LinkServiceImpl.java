package com.slepeweb.cms.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.Link.LinkType;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class LinkServiceImpl extends BaseServiceImpl implements LinkService {
	
	private static Logger LOG = Logger.getLogger(LinkServiceImpl.class);
	private static final String SELECT_TEMPLATE = 
			"select i.*, s.name as sitename, s.hostname, it.id as typeid, it.name as typename, it.mimetype, " +
			"l.parentid, l.linktype, l.name as linkname, l.ordering, " +
			"t.id as templateid, t.name as templatename, t.forward " +
			"from item i " +
			"join site s on i.siteid = s.id " +
			"join itemtype it on i.typeid = it.id " +
			"left join template t on i.templateid=t.id " +
			"join link l on i.id = l.childid " +
			"where %s and i.deleted=0 " +
			"order by l.ordering";


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
		else {
			LOG.error(compose("Link not saved - insufficient data", l));
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
			
			LOG.info(compose("Updated link", l));
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
// return SELECT_TEMPLATE +  (this.config.isLiveDelivery() ? " and i.published = 1" : "");
	public List<Link> getLinks(Long parentId) {
		String sql = String.format(getSelectSql(SELECT_TEMPLATE), "l.parentid = ?");
		return this.jdbcTemplate.query(sql, new Object[] {parentId}, new RowMapperUtil.LinkMapper());		 
	}

	public List<Link> getBindings(Long parentId) {
		return getLinks(parentId, LinkType.binding.name());		 
	}

	public List<Link> getInlines(Long parentId) {
		return getLinks(parentId, LinkType.inline.name());		 
	}

	public List<Link> getRelations(Long parentId) {
		return getLinks(parentId, LinkType.relation.name());		 
	}

	private List<Link> getLinks(Long parentId, String linkType) {
		String sql = String.format(getSelectSql(SELECT_TEMPLATE), "l.parentid = ? and l.linktype = ?");
		return this.jdbcTemplate.query(sql, new Object[] {parentId, linkType}, new RowMapperUtil.LinkMapper());		 
	}

	public Link getLink(Long parentId, Long childId) {
		String sql = String.format(getSelectSql(SELECT_TEMPLATE), "l.parentid = ? and l.childid = ?");
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
		String sql = String.format(SELECT_TEMPLATE, "l.childid = ? and l.linktype = 'binding'");
		return (Link) getFirstInList(this.jdbcTemplate.query(sql, new Object[] {childId}, 
				new RowMapperUtil.LinkMapper()));
	}
}