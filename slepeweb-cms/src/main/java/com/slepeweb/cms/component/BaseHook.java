package com.slepeweb.cms.component;

import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.LinkName;
import com.slepeweb.cms.bean.LinkType;
import com.slepeweb.cms.bean.guidance.IGuidance;

public class BaseHook extends NoHook {
		
	@Autowired private IGuidance stdRelatedLinkGuidance;

	@Override
	public void addItemPre(Item i) {
		i.setPublished(true);
		i.setSearchable(true);
	}

	@Override
	public IGuidance getLinkDataGuidance(String type, String name) {
		if (type.equals(LinkType.relation) && name.equals(LinkName.std)) {
			return this.stdRelatedLinkGuidance;
		}

		return null;
	}
}
