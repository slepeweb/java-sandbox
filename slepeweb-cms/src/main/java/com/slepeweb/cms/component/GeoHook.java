package com.slepeweb.cms.component;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.service.FieldForTypeService;

public class GeoHook extends BaseHook {
	
	@Autowired private FieldForTypeService fieldForTypeService;

	
	@Override
	public void addItemPre(Item i) {
		super.addItemPre(i);
		
		boolean flag1 = false, flag2 = false;
		
		for (FieldForType fft : this.fieldForTypeService.getFieldsForType(i.getType().getId())) {
			if (flag1 && flag2) {
				break;
			}
			
			if (fft.getField().getVariable().equals(FieldName.CACHEABLE)) {
				i.setFieldValue(FieldName.CACHEABLE, true);
				flag1 = true;
			}
			else if (fft.getField().getVariable().equals(FieldName.HIDE_FROM_NAV)) {
				i.setFieldValue(FieldName.HIDE_FROM_NAV, false);
				flag2 = true;
			}
		}
	}

}
