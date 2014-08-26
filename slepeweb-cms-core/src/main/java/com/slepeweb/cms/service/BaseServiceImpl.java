package com.slepeweb.cms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.slepeweb.cms.component.Config;
import com.slepeweb.cms.utils.LogUtil;

public class BaseServiceImpl {
	
	//private static Logger LOG = Logger.getLogger(BaseServiceImpl.class);

	@Autowired protected JdbcTemplate jdbcTemplate;
	@Autowired protected Config config;
	
	protected String getSelectSql(String sql) {
		return sql +  (this.config.isLiveDelivery() ? " and i.published = 1" : "");
	}
	
	protected Long getLastInsertId() {
		return this.jdbcTemplate.queryForLong("select last_insert_id()");
	}
	
	protected <T> Object getFirstInList(List<T> list) {
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;

	}
	
	protected String compose(String template, Object ... params) {
		return LogUtil.compose(template, params);
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

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

}
