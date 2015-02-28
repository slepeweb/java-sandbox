package com.slepeweb.site.ntc.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CompetitionIndex {

	private List<Competition> competitions = new ArrayList<Competition>();

	public List<Fixture> getRecentResults() {
		List<Fixture> recentResults = new ArrayList<Fixture>();
		for (Competition c : getCompetitions()) {
			recentResults.addAll(c.getRecentResults());
		}
		
		Collections.sort(recentResults, new Comparator<Fixture>() {
			@Override
			public int compare(Fixture f1, Fixture f2) {
				return f2.getDate().compareTo(f1.getDate());
			}
		});
		
		if (recentResults.size() > 6) {
			recentResults = recentResults.subList(0,  6);
		}
		
		return recentResults;
	}
	
	public Competition findCompetition(String path) {
		for (Competition c : getCompetitions()) {
			if (c.getItem() != null && c.getItem().getPath().equals(path)) {
				return c;
			}
		}
		
		return null;
	}
	
	public List<Competition> getCompetitions() {
		return competitions;
	}

	public void setCompetitions(List<Competition> competitions) {
		this.competitions = competitions;
	}
	
}
