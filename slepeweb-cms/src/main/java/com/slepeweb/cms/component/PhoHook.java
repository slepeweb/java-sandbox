package com.slepeweb.cms.component;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.guidance.IGuidance;
import com.slepeweb.cms.constant.FieldName;

public class PhoHook implements ICmsHook {
	
	@Autowired private IGuidance dateishFieldGuidance;

	@Override
	public void addItem(Item i) {
		// Do nothing
	}

	@Override
	public void updateFields(Item i) {
		// Do nothing
	}

	@Override
	public IGuidance getFieldGuidance(String variable) {
		if (variable.equals(FieldName.DATEISH)) {
			return this.dateishFieldGuidance;
		}
		
		return null;
	}

	@Override
	public IGuidance getLinknameGuidance(String linkname) {
		return null;
	}

	@Override
	public String getSitename() {
		return "pho";
	}

}
