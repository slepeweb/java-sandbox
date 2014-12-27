package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.LoggerBean;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class LoglevelServiceImpl extends BaseServiceImpl implements LoglevelService {
	
	private static Logger LOG = Logger.getLogger(LoglevelServiceImpl.class);
	
	public LoggerBean save(LoggerBean t) {
		if (t.isDefined4Insert()) {
			LoggerBean dbRecord = getLogger(t.getPackag());		
			if (dbRecord != null) {
				updateLogger(dbRecord, t);
				return dbRecord;
			}
			else {
				insertLogger(t);
			}
		}
		else {
			LOG.error(compose("Logger bean not saved - insufficient data", t));
		}
		
		return t;
	}
	
	private void insertLogger(LoggerBean lb) {
		this.jdbcTemplate.update(
				"insert into loglevel (package, level) values (?, ?)", 
				lb.getPackag(), lb.getLevel());

		//this.cacheEvictor.evict(lb);
		LOG.info(compose("Added new logger", lb));
	}

	private void updateLogger(LoggerBean dbRecord, LoggerBean lb) {
		if (! dbRecord.equals(lb)) {
			//this.cacheEvictor.evict(dbRecord);
			dbRecord.assimilate(lb);
			
			this.jdbcTemplate.update(
					"update loglevel set level = ? where package = ?", 
					lb.getLevel(), lb.getPackag());
			
			LOG.info(compose("Updated logger bean", lb));
		}
		else {
			LOG.info(compose("Logger not modified", lb));
		}
	}
	
	public void deleteLogger(String pkg) {
		if (this.jdbcTemplate.update("delete from loglevel where package = ?", pkg) > 0) {
			LOG.warn(compose("Deleted logger", pkg));
		}
	}
	
	public List<LoggerBean> getAllLoggers() {
		return (List<LoggerBean>) this.jdbcTemplate.query("select * from loglevel order by package", 
				new RowMapperUtil.LoglevelMapper());
	}
	
	public LoggerBean getLogger(String pkg) {
		return (LoggerBean) this.jdbcTemplate.query("select * from loglevel where package = ?", 
				new RowMapperUtil.LoglevelMapper());
	}
	
}
