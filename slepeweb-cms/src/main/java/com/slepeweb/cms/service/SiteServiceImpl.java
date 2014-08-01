package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class SiteServiceImpl extends BaseServiceImpl implements SiteService {
	
	private static Logger LOG = Logger.getLogger(SiteServiceImpl.class);
	@Autowired protected ItemTypeService itemTypeService;	
	@Autowired protected ItemService itemService;	
	
	public Site save(Site s) {
		return save(s, null);
	}
	
	public Site save(Site s, Item homepageItem) {
		if (s.isDefined4Insert()) {
			Site dbRecord = getSite(s.getName());		
			if (dbRecord != null) {
				updateSite(dbRecord, s);
			}
			else {
				insertSite(s, homepageItem);
			}
		}
		
		return s;
	}
	
	private Site insertSite(Site s, Item homepageItem) {
		
		this.jdbcTemplate.update(
				"insert into site (name, hostname) values (?, ?)", 
				s.getName(), s.getHostname());
		
		s.setId(getLastInsertId());
		
		if (homepageItem != null) {
			this.itemService.save(homepageItem);
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

	public Site getSite(String name) {
		return getSite("select * from site where name = ?", new Object[]{name});
	}

	public Site getSite(Long id) {
		return getSite("select * from site where id = ?", new Object[]{id});
	}
	
	private Site getSite(String sql, Object[] params) {
		List<Site> group = this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.SiteMapper());
		
		if (group.size() > 0) {
			Site s = group.get(0);
			return s;
		}
		else {
			return null;
		}
	}

	public List<Site> getAllSites() {
		return this.jdbcTemplate.query(
			"select * from site", new RowMapperUtil.SiteMapper());
	}

}
