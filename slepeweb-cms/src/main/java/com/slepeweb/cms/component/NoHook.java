package com.slepeweb.cms.component;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.guidance.IGuidance;

public class NoHook implements ICmsHook {
	
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
		return null;
	}

	@Override
	public IGuidance getLinknameGuidance(String linkname) {
		return null;
	}
}
