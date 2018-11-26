package com.slepeweb.money.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class BaseServiceImpl {
	
	@Autowired protected JdbcTemplate jdbcTemplate;	
	
	@SuppressWarnings("deprecation")
	protected Long getLastInsertId() {
		return this.jdbcTemplate.queryForLong("select last_insert_id()");
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

	public static String compose(String template, Object ... args) {
		StringBuilder sb = new StringBuilder(template).append(" [");
		for (int i = 0; i < args.length; i++) {
			if (i > 0 && i < args.length) {
				sb.append(" / ");
			}
			sb.append("%s");
		}
		sb.append("]");
		return String.format(sb.toString(), args);
	}

	protected String error(Logger log, String s, Object o) {
		log.error(compose(s, o.toString()));
		return s;
	}
}
