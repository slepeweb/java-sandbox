package com.slepeweb.cms.component;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.guidance.IGuidance;

public interface ICmsHook {
	void addItem(Item i);
	void updateFields(Item i);
	IGuidance getFieldGuidance(String variable);
	IGuidance getLinknameGuidance(String name);
}
