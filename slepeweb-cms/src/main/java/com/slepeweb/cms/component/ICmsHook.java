package com.slepeweb.cms.component;

import com.slepeweb.cms.bean.Item;

public interface ICmsHook {
	void addItem(Item i);
	void updateFields(Item i);
}
