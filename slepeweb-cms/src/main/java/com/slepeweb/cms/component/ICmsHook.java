package com.slepeweb.cms.component;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.guidance.IGuidance;

public interface ICmsHook {
	void addItemPost(Item i);
	void updateLinksPost(Item i);
	IGuidance getFieldGuidance(String variable);
	IGuidance getLinknameGuidance(String name);
}
