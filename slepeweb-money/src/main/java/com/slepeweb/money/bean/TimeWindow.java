package com.slepeweb.money.bean;

import java.sql.Timestamp;
import java.util.Date;

public class TimeWindow {
	private Timestamp from, to;
	
	public TimeWindow() {
		this.from = new Timestamp(0L);
		this.to = new Timestamp(new Date().getTime());
	}
	
	public TimeWindow(Timestamp from, Timestamp to) {
		this.from = from;
		this.to= to;
	}
	
	public boolean wraps(Timestamp t) {
		return t.after(this.from) && t.before(this.to);
	}

	public Timestamp getFrom() {
		return from;
	}

	public void setFrom(Timestamp from) {
		this.from = from;
	}

	public Timestamp getTo() {
		return to;
	}

	public void setTo(Timestamp to) {
		this.to = to;
	}
}
