package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class LinkTypeServiceImpl extends BaseServiceImpl implements LinkTypeService {
	
	private static Logger LOG = Logger.getLogger(LinkTypeServiceImpl.class);

	public LinkType save(LinkType lt) {
		if (lt.isDefined4Insert()) {
			LinkType dbRecord = getLinkType(lt.getName());		
			if (dbRecord != null) {
				updateLinkType(dbRecord, lt);
				return dbRecord;
			}
			else {
				insertLinkType(lt);
			}
		}
		else {
			LOG.error(compose("LinkType not saved - insufficient data", lt));
		}
		
		return lt;
	}
	
	private void insertLinkType(LinkType lt) {
		this.jdbcTemplate.update("insert into linktype (name) values (?)", lt.getName());
		
		lt.setId(getLastInsertId());
		this.cacheEvictor.evict(lt);
		LOG.info(compose("Added new link type", lt));
	}
	
	private void updateLinkType(LinkType dbRecord, LinkType lt) {
		if (! dbRecord.equals(lt)) {
			this.cacheEvictor.evict(dbRecord);
			dbRecord.assimilate(lt);
			
			this.jdbcTemplate.update(
					"update linktype set name = ? where id = ?", 
					dbRecord.getName(), dbRecord.getId());
			
			LOG.info(compose("Updated link", lt));
		}
		else {
			LOG.debug(compose("Link not modified", lt));
		}
	}

	public void deleteLinkType(LinkType lt) {
		if (this.jdbcTemplate.update("delete from linktype where id = ?", lt.getId()) > 0) {
			LOG.warn(compose("Deleted linktype", String.valueOf(lt.getId())));
			this.cacheEvictor.evict(lt);
		}
	}

	@Cacheable(value="serviceCache")
	public LinkType getLinkType(String name) {
		return (LinkType) getFirstInList(this.jdbcTemplate.query("select * from linktype where name = ?", 
				new Object[] {name}, new RowMapperUtil.LinkTypeMapper()));		 
	}

	@Cacheable(value="serviceCache")
	public List<LinkType> getLinkTypes() {
		return this.jdbcTemplate.query("select * from linktype", new RowMapperUtil.LinkTypeMapper());		 
	}

	public int getCount() {
		return 1;
	}
	
}
