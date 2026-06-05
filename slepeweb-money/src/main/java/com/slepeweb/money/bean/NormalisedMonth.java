package com.slepeweb.money.bean;

import java.sql.Date;
import java.time.LocalDate;

import com.slepeweb.money.Util;

public class NormalisedMonth {
	private int index;

	public NormalisedMonth(NormalisedMonth nm) {
		this(nm.getIndex());
	}
	
	public NormalisedMonth(int i) {
		this.index = i;
	}
	
	public NormalisedMonth(Date ld) {
		this(ld.toLocalDate());
	}
	
	public NormalisedMonth(LocalDate c) {
		LocalDate today = Util.today();
		
		this.index = 
				((today.getYear() - c.getYear()) * 12) +
				(today.getMonthValue() - c.getMonthValue()) +
				1;			
	}
	
	public String toString() {
		return String.format("Month %d", getIndex());
	}
	
	public void set(NormalisedMonth nm) {
		this.index = nm.getIndex();
	}
	
	public void increment(int number) {
		this.index -= number;
	}
	
	public void decrement(int number) {
		this.index += number;
	}
	
	public int getCalendarOffset() {
		return -(getIndex() - 1);
	}
	
	public int getYear() {
		LocalDate ld = Util.today().plusMonths(getCalendarOffset());
		return ld.getYear();
	}

	public boolean equals(NormalisedMonth nm) {
		return getIndex() == nm.getIndex();
	}
	
	public int distanceAfter(NormalisedMonth nm) {
		return nm.getIndex() - getIndex();
	}
	
	public int distanceBefore(NormalisedMonth nm) {
		return -distanceAfter(nm);
	}
	
	public boolean isBefore(NormalisedMonth nm) {
		return getIndex() > nm.getIndex();
	}
	
	public boolean isOnOrBefore(NormalisedMonth nm) {
		return getIndex() >= nm.getIndex();
	}
	
	public boolean isAfter(NormalisedMonth nm) {
		return getIndex() < nm.getIndex();
	}
	
	public boolean isOnOrAfter(NormalisedMonth nm) {
		return getIndex() <= nm.getIndex();
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int i) {
		this.index = i;
	}

}
