package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TagList {
	private int MAXSIZE = 32;
	private static String[] COLORS = {
			"red", "green", "blue", "orange", "mauve"
	};
	
	private List<TagCount> list = new ArrayList<TagCount>();
	private int max;
	private boolean analyzed;

	public TagList(int size) {
		this.max = size > 0 ? size : 1;
	}
	
	public boolean isAnalyzed() {
		return analyzed;
	}

	public int getMax() {
		return max;
	}

	public List<TagCount> getList() {
		if(isAnalyzed()) {
			return list;
		}
		
		return analyze();
	}

	public void inc(TagCount t) {
		inc(t.getValue());
	}
	
	public void inc(String value) {
		TagCount t = new TagCount(value);
		int index = this.list.indexOf(t);
		this.analyzed = false;
		
		if (index < 0) {
			this.list.add(t);
			t.inc();
		}
		else {
			this.list.get(index).inc();
		}
	}
	
	public List<TagCount> analyze() {
		this.list.sort(new Comparator<TagCount>() {

			@Override
			public int compare(TagCount o1, TagCount o2) {
				long diff = o2.getCount() - o1.getCount();
				if (diff > 0L) {
					return 1;
				}
				else if (diff < 0L) {
					return -1;
				}
				return 0;
			}
			
		});
		
		this.analyzed = true;
		this.list = this.list.subList(0, this.max - 1);
		
		// Calculated display styles
		TagCount tc;
		long maxCount = this.list.get(0).getCount();
		float fraction;
		
		
		for (int i = 0; i < this.list.size(); i++) {
			tc = this.list.get(i);
			fraction = (float) tc.getCount() / maxCount;
					
			tc.setLeft(i * 24);
			tc.setTop((i * 24) * (i % 2 == 0 ? 1 : -1));
			tc.setSize((int) (fraction * MAXSIZE));
			tc.setColor(COLORS[i % COLORS.length]);
		}
		
		return this.list;
	}
}
