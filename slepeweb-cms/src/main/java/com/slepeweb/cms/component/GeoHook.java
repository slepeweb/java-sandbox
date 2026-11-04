package com.slepeweb.cms.component;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.service.FieldForTypeService;

public class GeoHook extends BaseHook {
	
	@Autowired private FieldForTypeService fieldForTypeService;

	
	@Override
	public void addItemPost(Item i) {
		boolean flag1 = false, flag2 = false, flag3 = false;
		List<FieldForType> fields4Type = this.fieldForTypeService.getFieldsForType(i.getType().getId());
		
		for (FieldForType fft : fields4Type) {
			if (flag1 && flag2 && flag3) {
				break;
			}
			
			if (fft.getField().getVariable().equals(FieldName.CACHEABLE)) {
				i.setFieldValue(FieldName.CACHEABLE, "yes");
				flag1 = true;
			}
			else if (fft.getField().getVariable().equals(FieldName.HIDE_FROM_NAV)) {
				i.setFieldValue(FieldName.HIDE_FROM_NAV, "no");
				flag2 = true;
			}
			else if (fft.getField().getVariable().equals(FieldName.TITLE)) {
				i.setFieldValue("title", i.getName());
				flag3 = true;
			}
		}
		
		if (flag1 || flag2 || flag3) {
			try {
				i.saveFieldValues();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
