package com.slepeweb.cms.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class SiteServiceImpl extends BaseServiceImpl implements SiteService {
	
	private static Logger LOG = Logger.getLogger(SiteServiceImpl.class);
	@Autowired protected ItemTypeService itemTypeService;	
	@Autowired protected ItemService itemService;	
	
	public void insertSite(Site s) {
		
		if (s.isDefined4Insert()) {
			String rootName = "Root";
			ItemType type = this.itemTypeService.getItemType(rootName);
			
			if (type != null) {
				this.jdbcTemplate.update(
						"insert into site (name, hostname) values (?, ?)", 
						s.getName(), s.getHostname());
				
				s.setId(getLastInsertId());
				Item r = new Item();
				r.setName(rootName);
				r.setSimpleName("");
				r.setPath("/");
				r.setSite(s);
				r.setType(type);
				r.setDateCreated(new Timestamp(System.currentTimeMillis()));
				r.setDateUpdated(r.getDateCreated());
				r.setDeleted(false);
				this.itemService.insertItem(r);
				
				LogUtil.info(LOG, "Added new site", s.getName());
			}
			else {
				LogUtil.info(LOG, "No root item type defined", "Root");
			}
		}
	}

	public void updateSite(Site site) {
		if (site.isDefined4Insert()) {
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
			Site s = group.get(0);
			return s;
		}
		else {
			return null;
		}
	}

	@Override
	public List<Site> getAllSites() {
		// TODO Auto-generated method stub
		return null;
	}

}
