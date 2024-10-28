package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class LinkNameServiceImpl extends BaseServiceImpl implements LinkNameService {
	
	private static Logger LOG = Logger.getLogger(LinkNameServiceImpl.class);

	public LinkName save(LinkName ln) {
		if (ln.isDefined4Insert()) {
			LinkName dbRecord = getLinkName(ln.getSiteId(), ln.getLinkTypeId(), ln.getName());		
			if (dbRecord != null) {
				updateLinkName(dbRecord, ln);
				return dbRecord;
			}
			else {
				insertLinkName(ln);
			}
		}
		else {
			LOG.error(compose("LinkName not saved - insufficient data", ln));
		}
		
		return ln;
	}
	
	private void insertLinkName(LinkName ln) {
		this.jdbcTemplate.update("insert into linkname (siteid, linktypeid, name) values (?, ?, ?)",
				ln.getSiteId(), ln.getLinkTypeId(), ln.getName());
		
		ln.setId(getLastInsertId());
		LOG.info(compose("Added new link name", ln));
	}
	
	private void updateLinkName(LinkName dbRecord, LinkName ln) {
		if (! dbRecord.equals(ln)) {
			dbRecord.assimilate(ln);
			
			this.jdbcTemplate.update("update linkname set name = ? where id = ?", 
					dbRecord.getName(), dbRecord.getId());
			
			LOG.info(compose("Updated linkname", ln));
		}
		else {
			LOG.debug(compose("Linkname not modified", ln));
		}
	}

	public void deleteLinkName(LinkName ln) {
		if (this.jdbcTemplate.update("delete from linkname where id = ?", ln.getId()) > 0) {
			LOG.warn(compose("Deleted linkname", String.valueOf(ln.getId())));
		}
	}

	public LinkName getLinkName(Long siteId, Long linkTypeId, String name) {
		return (LinkName) getFirstInList(
				this.jdbcTemplate.query("select * from linkname where siteid = ? and linktypeid = ? and name = ?", 
						new RowMapperUtil.LinkNameMapper(), siteId, linkTypeId, name));		 
	}

	public List<LinkName> getLinkNames(Long siteId, Long linkTypeId) {
		return this.jdbcTemplate.query("select * from linkname where siteid = ? and linktypeid = ?", 
						new RowMapperUtil.LinkNameMapper(), siteId, linkTypeId);		 
	}

	public int getCount() {
		return 1;
	}
	
}
