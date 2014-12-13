package com.slepeweb.site.sws.spizza.bean;

import java.io.Serializable;

import com.slepeweb.site.model.Page;

public class ModelBase implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Page page;

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

}
