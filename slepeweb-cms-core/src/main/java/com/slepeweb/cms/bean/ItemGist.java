package com.slepeweb.cms.bean;

import java.util.Date;

public class ItemGist extends ItemIdentifier {

	// Arbitrary date - used for sorting purposes.
	private Date date = new Date();
	
	public ItemGist(Item i) {
		super(i.getOrigId());
		
		this.
			setName(i.getName()).
			setPath(i.getPath()).
			setType(i.getType().getName());
	}
	
	public Date getDate() {
		return this.date;
	}

	public ItemGist setDate(Date d) {
		this.date = d;
		return this;
	}

}
