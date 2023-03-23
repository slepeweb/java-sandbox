package com.slepeweb.cms.component;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.guidance.IGuidance;
import com.slepeweb.cms.constant.FieldName;

public class AncHook extends NoHook {
	
	@Autowired private IGuidance ancPartnerLinkGuidance;
	
	@Override
	public void addItemPost(Item i) {
		// Set surname to be the same as that of the parent
		Item parent = i.getParent();
		String surname = parent.getFieldValue(FieldName.LASTNAME);
		i.setFieldValue(FieldName.LASTNAME, surname);
		
		// Use item name to guess firstname and middlenames
		String[] nameParts = i.getName().split("\\s");
		i.setFieldValue(FieldName.FIRSTNAME, nameParts[0]);
		
		if (nameParts.length > 1) {
			StringBuilder sb = new StringBuilder();
			
			for (int j = 1; j < nameParts.length; j++) {
				if (! nameParts[j].equals(surname)) {
					if (sb.length() > 0) {
						sb.append(" ");
					}
					sb.append(nameParts[j]);
				}
			}
			
			i.setFieldValue(FieldName.MIDDLENAMES, sb.toString());
		}
		
		try {
			i.saveFieldValues();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IGuidance getLinknameGuidance(String linkname) {
		if (linkname.equals("partner")) {
			return this.ancPartnerLinkGuidance;
		}

		return null;
	}
}
