package com.slepeweb.cms.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class SiteServiceImpl extends BaseServiceImpl implements SiteService {
	
	private static Logger LOG = Logger.getLogger(SiteServiceImpl.class);
	@Autowired protected ItemTypeService itemTypeService;	
	@Autowired protected ItemService itemService;	
	
	public Site save(Site s) throws ResourceException {
		if (s.isDefined4Insert()) {
			Site dbRecord = getSite(s.getName());		
			if (dbRecord != null) {
				updateSite(dbRecord, s);
				return dbRecord;
			}
			else {
				insertSite(s);
			}
		}
		else {
			String t = "Site not saved - insufficient data";
			LOG.error(compose(t, s));
			throw new MissingDataException(t);
		}
		
		return s;
	}
	
	private Site insertSite(Site s) throws ResourceException {
		this.jdbcTemplate.update(
				"insert into site (name, shortname, language, xlanguages, secured) values (?, ?, ?, ?, ?)", 
				s.getName(), s.getShortname(), s.getLanguage(), s.getExtraLanguages(), s.isSecured());
		
		s.setId(getLastInsertId());	
		
		CmsBeanFactory.makeHomepageItem(s);
		ItemType cfolderType = this.itemTypeService.getItemType(ItemType.CONTENT_FOLDER_TYPE_NAME);
		
		if (cfolderType != null) {
			CmsBeanFactory.makeContentFolderRootItem(s, cfolderType);
		}
		else {
			LOG.error(compose("Content folders could not be created", ItemType.CONTENT_FOLDER_TYPE_NAME));
		}
		
		LOG.info(compose("Added new site", s));		
		return s;
	}

	private void updateSite(Site dbRecord, Site site) {
		if (! dbRecord.equals(site)) {
			dbRecord.assimilate(site);
			
			this.jdbcTemplate.update(
					"update site set name = ?, shortname = ?, language = ?, xlanguages = ?, secured = ? where id = ?", 
					dbRecord.getName(), dbRecord.getShortname(), dbRecord.getLanguage(), 
					dbRecord.getExtraLanguages(), dbRecord.isSecured(), dbRecord.getId());
			
			LOG.info(compose("Updated site", site));
		}
		else {
			site.setId(dbRecord.getId());
			LOG.info(compose("Site not modified", site));
		}
	}

	public void deleteSite(Site s) {
		if (this.jdbcTemplate.update("delete from site where id = ?", s.getId()) > 0) {
			LOG.warn(compose("Deleted site", s.getName()));
		}
	}

	public Site getSite(String name) {
		return getSite("select * from site where name = ?", name);
	}

	public Site getSiteByShortname(String name) {
		return getSite("select * from site where shortname = ?", name);
	}

	public Site getSite(Long id) {
		return getSite("select * from site where id = ?", id);
	}
	
	private Site getSite(String sql, Object... params) {
		return (Site) getFirstInList(this.jdbcTemplate.query(
			sql, new RowMapperUtil.SiteMapper(), params));
	}

	public List<Site> getAllSites() {
		return this.jdbcTemplate.query(
			"select * from site", new RowMapperUtil.SiteMapper());
	}

	public List<Site> getAllSites(User u, String role) {
		List<Site> list = new ArrayList<Site>();
		
		for (Site s : getAllSites()) {
			if (u.hasRole(s.getId(), role)) {
				list.add(s);
			}
		}
			
		return list;
	}

	public List<User> getContributors(long siteId) {
		return this.jdbcTemplate.query(
			"select * from user where id in (SELECT distinct ownerid from item where siteid=?) order by lastname, firstname", 
			new RowMapperUtil.UserMapper(), siteId);
	}
}
