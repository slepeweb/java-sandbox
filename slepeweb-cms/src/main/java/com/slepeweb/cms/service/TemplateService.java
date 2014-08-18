package com.slepeweb.cms.service;

import java.util.List;

import com.slepeweb.cms.bean.Template;


public interface TemplateService {
	void deleteTemplate(Long id);
	Template getTemplate(Long id);
	Template getTemplate(Long siteId, String name);
	List<Template> getAvailableTemplates(Long siteId);
	Template save(Template i);
	int getCount();
}
