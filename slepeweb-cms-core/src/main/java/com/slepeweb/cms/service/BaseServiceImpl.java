package com.slepeweb.cms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.slepeweb.cms.utils.LogUtil;

public class BaseServiceImpl {
	
	//private static Logger LOG = Logger.getLogger(BaseServiceImpl.class);

	@Autowired protected CmsService cmsService;
	@Autowired protected JdbcTemplate jdbcTemplate;
	
	protected String getVersionClause() {
		return this.cmsService.isDeliveryContext() ? " and i.published = 1" : " and i.editable = 1";
	}
	
	protected Long getLastInsertId() {
		return this.jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
	}
	
	protected <T> Object getFirstInList(List<T> list) {
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	protected <T> Object getLastInList(List<T> list) {
		if (list != null && list.size() > 0) {
			return list.get(list.size() - 1);
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
