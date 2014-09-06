package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
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
				"insert into template (name, forward, siteid, typeid) values (?, ?, ?, ?)", 
				t.getName(), t.getForward(), t.getSiteId(), t.getItemTypeId());

		t.setId(getLastInsertId());
		LOG.info(compose("Added new template", t));
	}

	private void updateTemplate(Template dbRecord, Template t) {
		if (! dbRecord.equals(t)) {
			dbRecord.assimilate(t);
			this.jdbcTemplate.update(
					"update template set name = ?, forward = ? where id = ?", 
					t.getName(), t.getForward(), t.getId());
			
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
	
	@Cacheable(value="serviceCache")
	public Template getTemplate(Long id) {
		return (Template) getFirstInList(
			this.jdbcTemplate.query("select * from template where id = ?", 
				new Object[]{id},
				new RowMapperUtil.TemplateMapper()));
	}
	
	@Cacheable(value="serviceCache")
	public Template getTemplate(Long siteId, String name) {
		return (Template) getFirstInList(
			this.jdbcTemplate.query("select * from template where siteid = ? and name = ?", 
				new Object[]{siteId, name},
				new RowMapperUtil.TemplateMapper()));
	}
	
	@Cacheable(value="serviceCache")
	public List<Template> getAvailableTemplates(Long siteId) {
		return (List<Template>) this.jdbcTemplate.query("select * from template where siteid = ? order by name", 
				new Object[]{siteId},
				new RowMapperUtil.TemplateMapper());
	}
	
	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from template");
	}

}
