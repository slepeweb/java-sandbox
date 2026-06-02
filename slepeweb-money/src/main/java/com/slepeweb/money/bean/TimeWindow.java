package com.slepeweb.money.bean;

import java.sql.Date;

import com.slepeweb.money.Util;

public class TimeWindow {
	private Date from, to;
	
	public TimeWindow() {
		this.from = new Date(0L);
		this.to = Util.todaySQ();
	}
	
	public TimeWindow(Date from, Date to) {
		this.from = from;
		this.to= to;
	}
	
	public boolean wraps(Date t) {
		return t.after(this.from) && t.before(this.to);
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}
}
