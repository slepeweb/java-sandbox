package com.slepeweb.cms.component;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.guidance.IGuidance;

public interface ICmsHook {
	// Actions to take before new item is saved
	void addItemPre(Item i);
	
	// Actions to take after new item is saved
	void addItemPost(Item i);
	
	// Actions to take after (non-orthogonal) links are saved
	void updateLinksPost(Item i);
	
	// Guidance on Fields tab
	IGuidance getFieldGuidance(String variable);
	
	// Guidance on link name options
	IGuidance getLinknameGuidance(String name);
}
