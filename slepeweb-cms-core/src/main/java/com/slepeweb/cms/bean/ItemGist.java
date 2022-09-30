package com.slepeweb.cms.bean;

import java.util.Date;

public class ItemGist extends ItemIdentifier {

	// Arbitrary date - can be used for sorting purposes.
	private Date date;
	
	public ItemGist(Item i) {
		super(i.getOrigId());
		setName(i.getName());
		setPath(i.getPath());
	}
	
	public Date getDate() {
		return this.date;
	}

	public ItemGist setDate(Date d) {
		this.date = d;
		return this;
	}

}
