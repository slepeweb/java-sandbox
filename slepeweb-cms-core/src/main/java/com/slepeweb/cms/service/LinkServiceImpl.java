package com.slepeweb.cms.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class LinkServiceImpl extends BaseServiceImpl implements LinkService {
	
	private static Logger LOG = Logger.getLogger(LinkServiceImpl.class);
	private static List<Link> EMPTY_LIST = new ArrayList<Link>();
	
	private static String CHILD_SELECT_TEMPLATE = 
			"select i.*, s.name as sitename, s.shortname as site_shortname, s.language, s.xlanguages, s.secured, " +
			"it.id as typeid, it.name as typename, it.mimetype, it.privatecache, it.publiccache, " +
			"l.parentid, lt.name as linktype, ln.name as linkname, l.ordering, l.data, " +
			"t.id as templateid, t.name as templatename, t.forward " +
			"from item i " +
			"join site s on i.siteid = s.id " +
			"join itemtype it on i.typeid = it.id " +
			"left join template t on i.templateid=t.id " +
			"join link l on i.id = l.childid " +
			"join linktype lt on l.linktypeid = lt.id " +
			"join linkname ln on l.linknameid = ln.id " +
			"where %s " +
			"order by l.ordering";

	private static String PARENT_SELECT_TEMPLATE = reverseSql(CHILD_SELECT_TEMPLATE);
	
	private static String reverseSql(String sql) {
		String tmp = "zz-temp";
		return sql.replace("l.parentid", tmp).
			replace("l.childid", "l.parentid").
			replace(tmp, "l.childid");
	}

	@Autowired private ItemService itemService;
	@Autowired private LinkTypeService linkTypeService;
	@Autowired private LinkNameService linkNameService;

	public Link save(Link l) throws ResourceException {
		if (l.isDefined4Insert()) {
			Link dbRecord = getLink(l.getParentId(), l.getChild().getId());		
			if (dbRecord != null) {
				updateLink(dbRecord, l);
				return dbRecord;
			}
			else {
				insertLink(l);
			}
		}
		else {
			String s = "Link not saved - insufficient data";
			LOG.error(compose(s, l));
			throw new MissingDataException(s);
		}
		
		return l;
	}
	
	private void insertLink(Link l) {
		Pair<LinkType, LinkName> pair = getLinkReferences(l);
		
		if (pair != null) {
			this.jdbcTemplate.update(
					"insert into link (parentid, childid, linktypeid, linknameid, ordering, data) values (?, ?, ?, ?, ?, ?)", 
					l.getParentId(), l.getChild().getId(), pair.getLeft().getId(), pair.getRight().getId(), l.getOrdering(), 
					l.getData());
			
			// Note: no new id generated for this bean
			LOG.info(compose("Added new link", l));
		}
	}
	
	private void updateLink(Link dbRecord, Link l) {
		if (! dbRecord.equals(l)) {
			dbRecord.assimilate(l);			
			Pair<LinkType, LinkName> pair = getLinkReferences(dbRecord);
			
			if (pair != null) {
				this.jdbcTemplate.update(
						"update link set linktypeid = ?, linknameid = ?, ordering = ?, data = ? where parentid = ? and childid = ?", 
						pair.getLeft().getId(), pair.getRight().getId(), dbRecord.getOrdering(), dbRecord.getData(),
						dbRecord.getParentId(), dbRecord.getChild().getId());
				
				LOG.info(compose("Updated link", l));
			}
		}
		else {
			LOG.debug(compose("Link not modified", l));
		}
	}
	
	private Pair<LinkType, LinkName> getLinkReferences(Link l) {
		Item parent = this.itemService.getItem(l.getParentId());
		LinkType lt = this.linkTypeService.getLinkType(l.getType());
		
		if (parent == null || lt == null) {
			LOG.error(compose("Failed to identify link parent/type", l));
			return null;
		}
		
		LinkName ln = this.linkNameService.getLinkName(parent.getSite().getId(), lt.getId(), l.getName());
		
		if (ln == null) {
			LOG.error(compose("Failed to identify link name", l));
			return null;
		}
		
		return Pair.of(lt, ln);
	}

	public void deleteLink(Long parentId, Long childId) {
		if (this.jdbcTemplate.update("delete from link where parentid = ? and childid = ?", parentId, childId) > 0) {
			LOG.warn(compose("Deleted links", String.valueOf(parentId) + " -> " + String.valueOf(childId)));
		}
	}

	public void deleteLinks(Long parentId, String linkType, String name) {
		LinkType lt = this.linkTypeService.getLinkType(linkType);
		if (lt != null) {		
			String sql = "delete from link where parentid = ? and linktypeid = ? ";
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
		else {
			LOG.error(compose("Failed to delete Links", parentId, linkType, name));
		}
	}


	public List<Link> getLinks(Long parentId) {
		String sql = String.format(CHILD_SELECT_TEMPLATE, "l.parentid = ? and i.deleted = 0" + getVersionClause());
		return this.jdbcTemplate.query(sql, new RowMapperUtil.LinkMapper(), parentId);		 
	}

	public List<Link> getParentLinks(Long childId) {
		String sql = String.format(PARENT_SELECT_TEMPLATE, "l.childid = ? and i.deleted = 0" + getVersionClause());
		return this.jdbcTemplate.query(sql, new RowMapperUtil.ParentLinkMapper(), childId);
	}

	public List<Link> getBindings(Long parentId) {
		return getLinks(parentId, "binding");		 
	}

	public List<Link> getInlines(Long parentId) {
		return getLinks(parentId, "inline");		 
	}

	public List<Link> getRelations(Long parentId) {
		return getLinks(parentId, "relation");		 
	}

	public List<Link> getComponents(Long parentId) {
		return getLinks(parentId, "component");		 
	}

	public List<Link> getLinks(Long parentId, String linkType) {
		LinkType lt = this.linkTypeService.getLinkType(linkType);
		if (lt != null) {
			String sql = String.format(CHILD_SELECT_TEMPLATE, "l.parentid = ? and l.linktypeid = ? and i.deleted = 0" + getVersionClause());
			return this.jdbcTemplate.query(sql, new RowMapperUtil.LinkMapper(), parentId, lt.getId());	
		}
		else {
			LOG.error(compose("Failed to retrieve Links", parentId, linkType));
			return EMPTY_LIST;
		}
	}

	/*
	 * TODO: This service is not used. Remove some time.
	 */
	public List<Link> getBindings2TrashedItems(Long parentId) {
		LinkType lt = this.linkTypeService.getLinkType("binding");
		String sql = String.format(CHILD_SELECT_TEMPLATE, "l.parentid = ? and l.linktypeid = ? and deleted = 1");
		return this.jdbcTemplate.query(sql, new RowMapperUtil.LinkMapper(), parentId, lt.getId());	
	}

	public Link getLink(Long parentId, Long childId) {
		String sql = String.format(CHILD_SELECT_TEMPLATE, "l.parentid = ? and l.childid = ? and i.deleted = 0" 
				/* Not necessary for id's  + getVersionClause(parent.isStaging())*/);
		return (Link) getFirstInList(this.jdbcTemplate.query(sql, 
				new RowMapperUtil.LinkMapper(), parentId, childId));
	}
	
	public int getCount() {
		return getCount(null);
	}
	
	public int getCount(Long parentId) {
		if (parentId != null) {
			return this.jdbcTemplate.queryForObject("select count(*) from link where parentid = ?", Integer.class, parentId);
		}
		else {
			return this.jdbcTemplate.queryForObject("select count(*) from link", Integer.class);
		}
	}
	
	public List<Link> getRelatedParents(Long childId) {
		LinkType lt = this.linkTypeService.getLinkType(LinkType.relation);
		if (lt != null) {		
			String sql = String.format(PARENT_SELECT_TEMPLATE, "l.childid = ? and l.linktypeid = ? and i.deleted = 0" + 
					getVersionClause());
			return this.jdbcTemplate.query(sql, new RowMapperUtil.ParentLinkMapper(), childId, lt.getId());
		}
		return null;
	}
}
