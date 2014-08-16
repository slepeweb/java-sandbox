package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.Template;


public interface TemplateService {
	void deleteTemplate(Long id);
	Template getTemplate(Long id);
	Template getTemplate(Long siteId, String name);
	Template save(Template i);
	int getCount();
}
