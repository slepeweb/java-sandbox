package com.slepeweb.money.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.slepeweb.money.service.Util;

public class Pager {

		private NormalisedMonth selectedMonth, firstMonth, lastMonth;
		
		public Pager(NormalisedMonth f, NormalisedMonth c, NormalisedMonth l) {
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
			
			for (int i = 0; i < 5; i++) {
				if (nm.isOnOrAfter(getFirstMonth()) && nm.isOnOrBefore(getLastMonth())) {
					o = new Option(nm.getIndex(), sdf.format(current.getTime()));
					
					if (nm.equals(getSelectedMonth())) {
						o.setSelected(true);
						o.setName(o.getName() + " " + String.valueOf(current.get(Calendar.YEAR)));
					}
					
					options.add(o);
				}
				
				current.add(Calendar.MONTH, 1);
				nm = nm.increment(1);
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
				return new NormalisedMonth(getSelectedMonth()).increment(1);
			}
			
			return getSelectedMonth();
		}
		
		public NormalisedMonth getPreviousMonth() {
			if (isPrevious()) {
				return new NormalisedMonth(getSelectedMonth()).decrement(1);
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
