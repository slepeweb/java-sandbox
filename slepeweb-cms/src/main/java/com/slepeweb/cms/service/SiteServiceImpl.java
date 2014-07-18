package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class SiteServiceImpl extends BaseServiceImpl implements SiteService {
	
	private static Logger LOG = Logger.getLogger(SiteServiceImpl.class);
	
	public void addSite(Site s) {
		if (isDefined(s)) {
			this.jdbcTemplate.update(
					"insert into site (name, hostname) values (?, ?)", 
					s.getName(), s.getHostname());
			
			Long rootItemId = getLastInsertId();
			
			this.jdbcTemplate.update(
					"insert into item (name, path, siteid) values ('Root', '/', ?)", 
					rootItemId);
			
			LogUtil.info(LOG, "Added new site", s.getName());
		}
	}

	public void updateSite(Site site) {
		if (isDefined(site)) {
			Site dbRecord = getSite(site.getId());
			
			if (dbRecord != null) {
				dbRecord.assimilate(site);
				
				this.jdbcTemplate.update(
						"update site set name = ?, hostname = ? where id = ?", 
						dbRecord.getName(), dbRecord.getHostname(), dbRecord.getId());
				
				LogUtil.info(LOG, "Updated site", site.getName());
			}
			else {
				LogUtil.warn(LOG, "Site not found", site.getName());
			}
		}
	}

	public void deleteSite(Long id) {
		if (this.jdbcTemplate.update("delete from site where id = ?", id) > 0) {
			LogUtil.warn(LOG, "Deleted site", String.valueOf(id));
		}
	}

	public void deleteSite(Site s) {
		deleteSite(s.getId());
	}

	public Site getSite(String name) {
		return getSite("select * from site where name = ?", new Object[]{name});
	}

	public Site getSite(Long id) {
		return getSite("select * from sitegroup where id = ?", new Object[]{id});
	}
	
	private Site getSite(String sql, Object[] params) {
		List<Site> group = this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.SiteMapper());
		
		if (group.size() > 0) {
			return group.get(0);
		}
		else {
			return null;
		}
	}

}
