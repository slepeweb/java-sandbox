package com.slepeweb.cms.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.utils.LogUtil;

public class BaseServiceImpl {
	
	private static Logger LOG = Logger.getLogger(BaseServiceImpl.class);

	@Autowired protected JdbcTemplate jdbcTemplate;

	protected Long getLastInsertId() {
		return this.jdbcTemplate.queryForLong("select last_insert_id()");
	}
	
	protected String placeholders4Insert(String fields) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fields.split(",").length; i++) {
			sb.append("?, ");
		}
		
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 2);
		}
		
		return "";
	}

	protected String placeholders4Update(String fields) {
		StringBuilder sb = new StringBuilder();
		for (String s : fields.split(",")) {
			sb.append(s + " = ?, ");
		}
		
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 2);
		}
		
		return "";
	}
	
}
