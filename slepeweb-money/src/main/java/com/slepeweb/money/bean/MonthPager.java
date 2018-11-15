package com.slepeweb.money.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.slepeweb.money.Util;

public class MonthPager {

	private static int BLOCK = 5;
	private NormalisedMonth selectedMonth, firstMonth, lastMonth;
	
	public MonthPager(NormalisedMonth f, NormalisedMonth c, NormalisedMonth l) {
		this.firstMonth = f;
		this.selectedMonth = c;
		this.lastMonth = l;
	}
	
	public List<Option> getNavigation() {
		List<Option> options = new ArrayList<Option>();
		Calendar current = Util.today();
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
		Option o;
		NormalisedMonth nm = new NormalisedMonth(getSelectedMonth());
		nm.decrement(2);
		current.add(Calendar.MONTH, nm.getCalendarOffset());
		
		for (int i = 0; i < BLOCK; i++) {
			if (nm.isOnOrAfter(getFirstMonth()) && nm.isOnOrBefore(getLastMonth())) {
				o = new Option(nm.getIndex(), sdf.format(current.getTime()));
				
				if (nm.equals(getSelectedMonth())) {
					o.setSelected(true);
					o.setName(o.getName() + " " + String.valueOf(current.get(Calendar.YEAR)));
				}
				
				options.add(o);
			}
			
			current.add(Calendar.MONTH, 1);
			nm.increment(1);
		}
		
		return options;
	}
	
	public NormalisedMonth getSelectedMonth() {
		return selectedMonth;
	}

	public NormalisedMonth getFirstMonth() {
		return firstMonth;
	}

	public NormalisedMonth getLastMonth() {
		return lastMonth;
	}

	public NormalisedMonth getNextMonth() {
		if (isNext()) {
			NormalisedMonth nm = new NormalisedMonth(getSelectedMonth());
			nm.increment(1);
			return nm;
		}
		
		return getSelectedMonth();
	}
	
	public NormalisedMonth getPreviousMonth() {
		if (isPrevious()) {
			NormalisedMonth nm = new NormalisedMonth(getSelectedMonth());
			nm.decrement(1);
			return nm;
		}
		
		return getSelectedMonth();
	}
	
	public NormalisedMonth getNextBlock() {
		if (isNext()) {
			NormalisedMonth nm = new NormalisedMonth(getSelectedMonth());
			nm.increment(BLOCK);
			while (nm.isAfter(getLastMonth())) {
				nm.decrement(1);
			}
			return nm;
		}
		
		return getSelectedMonth();
	}
	
	public NormalisedMonth getPreviousBlock() {
		if (isPrevious()) {
			NormalisedMonth nm = new NormalisedMonth(getSelectedMonth());
			nm.decrement(BLOCK);
			while (nm.isBefore(getFirstMonth())) {
				nm.increment(1);
			}
			return nm;
		}
		
		return getSelectedMonth();
	}
	
	public boolean isNext() {
		return getSelectedMonth().isBefore(getLastMonth()); 
	}
	
	public boolean isPrevious() {
		return getSelectedMonth().isAfter(getFirstMonth()); 
	}
}
