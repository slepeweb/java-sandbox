package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.SiteType;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class SiteTypeServiceImpl extends BaseServiceImpl implements SiteTypeService {
	
	private static Logger LOG = Logger.getLogger(SiteTypeServiceImpl.class);
	
	private static String SELECT_TEMPLATE = 
			"select it.id as typeid, it.name as typename, it.mimetype, it.privatecache, it.publiccache, s.id as siteid " +
			"from sitetype st " +
			"join site s on st.siteid = s.id " +
			"join itemtype it on st.typeid = it.id " +
			"where %s " +
			"order by it.name";

	public SiteType save(SiteType st) throws ResourceException {
		if (st.isDefined4Insert()) {
			SiteType dbRecord = get(st.getSiteId(), st.getType().getId());		
			if (dbRecord == null) {
				insert(st);
			}
		}
		else {
			String s = "Sitetype not saved - insufficient data";
			LOG.error(compose(s, st));
			throw new MissingDataException(s);
		}
		
		return st;
	}
	
	private void insert(SiteType l) {
		this.jdbcTemplate.update(
				"insert into sitetype (siteid, typeid) values (?, ?)", 
				l.getSiteId(), l.getType().getId());
		
		// Note: no new id generated for this bean
		LOG.info(compose("Added new Sitetype", l));
	}
	
	public void delete(Long siteId) {
		delete(siteId, null);
	}
	
	public void delete(Long siteId, Long typeId) {
			String sql = "delete from sitetype where siteid = ? ";
			if (typeId != null) {
				sql += "and typeid = ?";
				
				if (this.jdbcTemplate.update(sql, siteId, typeId) > 0) {
					LOG.warn(compose("Deleted Sitetype", String.valueOf(siteId) + " -> " + String.valueOf(typeId)));
				}
			}
			else {
				if (this.jdbcTemplate.update(sql, siteId) > 0) {
					LOG.warn(compose("Deleted links", String.valueOf(siteId)));
				}
			}	
	}


	public List<SiteType> get(Long siteId) {
		String sql = String.format(SELECT_TEMPLATE, "st.siteid = ?");
		return this.jdbcTemplate.query(sql, new RowMapperUtil.SiteTypeMapper(), siteId);		 
	}

	public SiteType get(Long siteId, Long typeId) {
		String sql = String.format(SELECT_TEMPLATE, "st.siteid = ? and st.typeid = ?" );
		return (SiteType) getFirstInList(this.jdbcTemplate.query(sql, 
				new RowMapperUtil.SiteTypeMapper(), siteId, typeId));
	}
	
}
