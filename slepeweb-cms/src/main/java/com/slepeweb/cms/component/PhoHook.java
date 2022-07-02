package com.slepeweb.cms.component;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.guidance.IGuidance;
import com.slepeweb.cms.constant.FieldName;

public class PhoHook extends NoHook {
	
	@Autowired private IGuidance dateishFieldGuidance;
	
	@Override
	public IGuidance getFieldGuidance(String variable) {
		if (variable.equals(FieldName.DATEISH)) {
			return this.dateishFieldGuidance;
		}
		
		return null;
	}
}
