package com.slepeweb.common.solr.bean;

import java.util.ArrayList;
import java.util.List;

public class SolrPager<T> {

	private static int BLOCK = 7;
	private int selectedPage, pageSize, maxPages;
	private long totalHits;
	
	public SolrPager(long totalHits, int pageSize, int selected) {
		this.totalHits = totalHits;
		this.pageSize = pageSize;
		this.selectedPage = selected;
		
		long n = Long.valueOf((totalHits - 1) / this.pageSize) + 1;
		this.maxPages = (int) n;
		
		if (this.selectedPage < 1) {
			this.selectedPage = 1;
		}
		
		if (this.selectedPage > this.maxPages) {
			this.selectedPage = this.maxPages;
		}
	}
	
	public List<Integer> getNavigation() {
		List<Integer> pageNumbers = new ArrayList<Integer>();
		int p = getSelectedPage() - Integer.valueOf(BLOCK / 2);
		
		for (int i = 0; i < BLOCK; i++) {
			if (p >= 1 && p <= getMaxPages()) {
				pageNumbers.add(p);
			}
			
			p++;
		}
		
		return pageNumbers;
	}
	
	public int getSelectedPage() {
		return selectedPage;
	}

	public int getNextSelection() {
		if (isNext()) {
			return getSelectedPage() + 1;
		}
		
		return getSelectedPage();
	}
	
	public int getPreviousSelection() {
		if (isPrevious()) {
			return getSelectedPage() - 1;
		}
		
		return getSelectedPage();
	}
	
	public int getNextBlock() {
		if (isNext()) {
			int p = getSelectedPage() + BLOCK;
			if (p > getMaxPages()) {
				p = getMaxPages();
			}
			return p;
		}
		
		return getSelectedPage();
	}
	
	public int getPreviousBlock() {
		if (isPrevious()) {
			int p = getSelectedPage() - BLOCK;
			if (p < 1) {
				p = 1;
			}
			return p;
		}
		
		return getSelectedPage();
	}
	
	public boolean isPrevious() {
		return getSelectedPage() > 1; 
	}
	
	public boolean isNext() {
		return getSelectedPage() < getMaxPages(); 
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getMaxPages() {
		return maxPages;
	}

	public boolean isVisible() {
		return getMaxPages() > 1;
	}
	
	public long getStartResultId() {
		return ((getSelectedPage() - 1) * getPageSize()) + 1;
	}
	
	public long getEndResultId() {
		long end = (getSelectedPage() * getPageSize());
		if (end > totalHits) {
			end = totalHits;
		}
		return end;
	}
}
