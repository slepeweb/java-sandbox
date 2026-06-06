package com.slepeweb.money.bean;

import java.sql.Date;
import java.time.LocalDate;

import com.slepeweb.money.Util;

public class TimeWindow {
	private LocalDate from, to;
	
	public TimeWindow() {
		this.from = new Date(0L).toLocalDate();
		this.to = Util.today();
	}
	
	public TimeWindow(LocalDate from, LocalDate to) {
		this.from = from;
		this.to= to;
	}
	
	public boolean wraps(LocalDate t) {
		return (t.isAfter(this.from) && t.isBefore(this.to)) || t.isEqual(this.from) || t.isEqual(this.to);
	}

	public LocalDate getFrom() {
		return from;
	}

	public void setFrom(LocalDate from) {
		this.from = from;
	}

	public LocalDate getTo() {
		return to;
	}

	public void setTo(LocalDate to) {
		this.to = to;
	}
}
