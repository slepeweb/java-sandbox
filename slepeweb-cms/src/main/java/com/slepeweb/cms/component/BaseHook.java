package com.slepeweb.cms.component;

import com.slepeweb.cms.bean.Item;

public class BaseHook extends NoHook {
		
	@Override
	public void addItemPre(Item i) {
		i.setPublished(true);
		i.setSearchable(true);
	}

}
