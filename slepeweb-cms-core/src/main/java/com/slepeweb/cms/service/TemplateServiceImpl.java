package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class TemplateServiceImpl extends BaseServiceImpl implements TemplateService {
	
	private static Logger LOG = Logger.getLogger(TemplateServiceImpl.class);
	
	public Template save(Template t) {
		if (t.isDefined4Insert()) {
			Template dbRecord = getTemplate(t.getSiteId(), t.getName());		
			if (dbRecord != null) {
				updateTemplate(dbRecord, t);
				return dbRecord;
			}
			else {
				insertTemplate(t);
			}
		}
		else {
			LOG.error(compose("Template not saved - insufficient data", t));
		}
		
		return t;
	}
	
	private void insertTemplate(Template t) {
		this.jdbcTemplate.update(
				"insert into template (name, forward, siteid, typeid, admin) values (?, ?, ?, ?, ?)", 
				t.getName(), t.getController(), t.getSiteId(), t.getItemTypeId(), t.isAdmin());

		t.setId(getLastInsertId());
		LOG.info(compose("Added new template", t));
	}

	private void updateTemplate(Template dbRecord, Template t) {
		if (! dbRecord.equals(t)) {
			dbRecord.assimilate(t);
			
			this.jdbcTemplate.update(
					"update template set name = ?, forward = ?, admin = ? where id = ?", 
					t.getName(), t.getController(), t.isAdmin(), t.getId());
			
			LOG.info(compose("Updated template", t));
		}
		else {
			t.setId(dbRecord.getId());
			LOG.info(compose("Template not modified", t));
		}
	}
	
	public void deleteTemplate(Long id) {
		if (this.jdbcTemplate.update("delete from template where id = ?", id) > 0) {
			LOG.warn(compose("Deleted template", String.valueOf(id)));
		}
	}
	
	public Template getTemplate(Long id) {
		return (Template) getFirstInList(
			this.jdbcTemplate.query("select * from template where id = ?", 
				new RowMapperUtil.TemplateMapper(), id));
	}
	
	public Template getTemplate(Long siteId, String name) {
		return (Template) getFirstInList(
			this.jdbcTemplate.query("select * from template where siteid = ? and name = ?", 
				new RowMapperUtil.TemplateMapper(), siteId, name));
	}
	
	public List<Template> getAvailableTemplates(Long siteId) {
		return (List<Template>) this.jdbcTemplate.query("select * from template where siteid = ? order by name", 
				new RowMapperUtil.TemplateMapper(), siteId);
	}
	
	public List<Template> getAvailableTemplates(Long siteId, Long itemTypeId) {
		return (List<Template>) this.jdbcTemplate.query("select * from template where siteid = ? and typeid = ? order by name", 
				new RowMapperUtil.TemplateMapper(), siteId, itemTypeId);
	}
	
	public int getCount() {
		return this.jdbcTemplate.queryForObject("select count(*) from template", Integer.class);
	}

}
