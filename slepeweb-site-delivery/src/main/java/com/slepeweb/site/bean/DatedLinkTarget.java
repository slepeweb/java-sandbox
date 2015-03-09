package com.slepeweb.site.bean;

import java.util.Date;

import com.slepeweb.site.model.LinkTarget;

public class DatedLinkTarget extends LinkTarget {

	private static final long serialVersionUID = 1L;
	private Date date;

	public TimeAgo getTimeAgo() {
		return TimeAgo.getInstance(this.date);
	}

	public DatedLinkTarget setDate(Date d) {
		this.date = d;
		return this;
	}
	
	public Date getDate() {
		return this.date;
	}
	
}
