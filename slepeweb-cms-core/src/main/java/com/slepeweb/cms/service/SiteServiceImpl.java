package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class SiteServiceImpl extends BaseServiceImpl implements SiteService {
	
	private static Logger LOG = Logger.getLogger(SiteServiceImpl.class);
	@Autowired protected ItemTypeService itemTypeService;	
	@Autowired protected ItemService itemService;	
	
	public Site save(Site s) {
		if (s.isDefined4Insert()) {
			Site dbRecord = getSite(s.getName());		
			if (dbRecord != null) {
				updateSite(dbRecord, s);
			}
			else {
				insertSite(s);
			}
		}
		else {
			LOG.error(compose("Site not saved - insufficient data", s));
		}
		
		return s;
	}
	
	private Site insertSite(Site s) {
		
		this.jdbcTemplate.update(
				"insert into site (name, hostname) values (?, ?)", 
				s.getName(), s.getHostname());
		
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
					"update site set name = ?, hostname = ? where id = ?", 
					dbRecord.getName(), dbRecord.getHostname(), dbRecord.getId());
			
			LOG.info(compose("Updated site", site));
		}
		else {
			site.setId(dbRecord.getId());
			LOG.info(compose("Site not modified", site));
		}
	}

	public void deleteSite(Long id) {
		if (this.jdbcTemplate.update("delete from site where id = ?", id) > 0) {
			LOG.warn(compose("Deleted site", String.valueOf(id)));
		}
	}

	public void deleteSite(String name) {
		if (this.jdbcTemplate.update("delete from site where name = ?", name) > 0) {
			LOG.warn(compose("Deleted site", name));
		}
	}

	@Cacheable(value="serviceCache")
	public Site getSite(String name) {
		return getSite("select * from site where name = ?", new Object[]{name});
	}

	@Cacheable(value="serviceCache")
	public Site getSiteByHostname(String hostname) {
		return getSite("select * from site where hostname = ?", new Object[]{hostname});
	}

	@Cacheable(value="serviceCache")
	public Site getSite(Long id) {
		return getSite("select * from site where id = ?", new Object[]{id});
	}
	
	private Site getSite(String sql, Object[] params) {
		return (Site) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.SiteMapper()));
	}

	public List<Site> getAllSites() {
		return this.jdbcTemplate.query(
			"select * from site", new RowMapperUtil.SiteMapper());
	}

}
