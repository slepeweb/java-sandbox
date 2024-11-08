package com.slepeweb.cms.component;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.guidance.IGuidance;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.service.SolrService4Cms;

public class PhoHook extends BaseHook {
	
	@Autowired private SolrService4Cms solrService4Cms;
	@Autowired private IGuidance dateishFieldGuidance;
	
	@Override
	public IGuidance getFieldGuidance(String variable) {
		if (variable.equals(FieldName.DATEISH)) {
			return this.dateishFieldGuidance;
		}
		
		return null;
	}
	
	@Override
	public void updateLinksPost(Item i) {
		this.solrService4Cms.save(i);
	}
}
