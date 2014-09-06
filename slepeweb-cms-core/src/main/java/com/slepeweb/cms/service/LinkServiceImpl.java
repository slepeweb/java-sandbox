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
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class LinkServiceImpl extends BaseServiceImpl implements LinkService {
	
	private static Logger LOG = Logger.getLogger(LinkServiceImpl.class);
	private static List<Link> EMPTY_LIST = new ArrayList<Link>();
	
	private static final String SELECT_TEMPLATE = 
			"select i.*, s.name as sitename, s.hostname, it.id as typeid, it.name as typename, it.mimetype, " +
			"l.parentid, lt.name as linktype, ln.name as linkname, l.ordering, " +
			"t.id as templateid, t.name as templatename, t.forward " +
			"from item i " +
			"join site s on i.siteid = s.id " +
			"join itemtype it on i.typeid = it.id " +
			"left join template t on i.templateid=t.id " +
			"join link l on i.id = l.childid " +
			"join linktype lt on l.linktypeid = lt.id " +
			"join linkname ln on l.linknameid = ln.id " +
			"where %s and i.deleted=0 " +
			"order by l.ordering";

	@Autowired private ItemService itemService;
	@Autowired private LinkTypeService linkTypeService;
	@Autowired private LinkNameService linkNameService;

	public Link save(Link l) {
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
			LOG.error(compose("Link not saved - insufficient data", l));
		}
		
		return l;
	}
	
	private void insertLink(Link l) {
		Pair<LinkType, LinkName> pair = getLinkReferences(l);
		
		if (pair != null) {
			this.jdbcTemplate.update(
					"insert into link (parentid, childid, linktypeid, linknameid, ordering) values (?, ?, ?, ?, ?)", 
					l.getParentId(), l.getChild().getId(), pair.getLeft().getId(), pair.getRight().getId(), l.getOrdering());
			
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
						"update link set linktypeid = ?, linknameid = ?, ordering = ? where parentid = ? and childid = ?", 
						pair.getLeft().getId(), pair.getRight().getId(), dbRecord.getOrdering(), 
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

	public void deleteLinks(Long parentId, Long childId) {
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
		String sql = String.format(getSelectSql(SELECT_TEMPLATE), "l.parentid = ?");
		return this.jdbcTemplate.query(sql, new Object[] {parentId}, new RowMapperUtil.LinkMapper());		 
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

	private List<Link> getLinks(Long parentId, String linkType) {
		LinkType lt = this.linkTypeService.getLinkType(linkType);
		if (lt != null) {
			String sql = String.format(getSelectSql(SELECT_TEMPLATE), "l.parentid = ? and l.linktypeid = ?");
			return this.jdbcTemplate.query(sql, new Object[] {parentId, linkType}, new RowMapperUtil.LinkMapper());	
		}
		else {
			LOG.error(compose("Failed to retrieve Links", parentId, linkType));
			return EMPTY_LIST;
		}
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
		LinkType lt = this.linkTypeService.getLinkType(LinkType.binding);
		if (lt != null) {		
			String sql = String.format(SELECT_TEMPLATE, "l.childid = ? and l.linktypeid = ?");
			return (Link) getFirstInList(this.jdbcTemplate.query(sql, new Object[] {childId, lt.getId()}, 
					new RowMapperUtil.LinkMapper()));
		}
		return null;
	}
}
