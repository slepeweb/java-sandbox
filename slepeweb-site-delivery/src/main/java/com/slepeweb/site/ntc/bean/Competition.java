package com.slepeweb.site.ntc.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.site.constant.FieldName;
import com.slepeweb.site.util.StringUtil;

public class Competition {

	private Item item;
	private String name, tableUrl;
	private List<Fixture> fixtures;
	private List<String> squad;
	
	public List<Fixture> getRecentResults() {
		List<Fixture> recentResults = new ArrayList<Fixture>();
		Date now = new Date();
		
		for (Fixture f : getFixtures()) {
			if (f.getDate().before(now) && f.getScoreFor() != null) {
				recentResults.add(f);
			}
		}
		
		return recentResults;
	}
	
	public String getName() {
		return name;
	}
	
	public Competition setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getTableUrl() {
		return tableUrl;
	}
	
	public Competition setTableUrl(String tableUrl) {
		this.tableUrl = tableUrl;
		return this;
	}
	
	public List<Fixture> getFixtures() {
		return fixtures;
	}
	
	public Competition setFixtures(List<Fixture> fixtures) {
		this.fixtures = fixtures;
		return this;
	}
	
	public Competition setFixtures(String str) {
		this.fixtures = new ArrayList<Fixture>();
		Fixture fix;
		
		for (String[] parts : StringUtil.splitLinesIntoParts(str, "[,]", 3)) {
			fix = new Fixture().
					setCompetition(this).
					setDate(parts[0]).
					setHome(parts[1]).
					setOpponent(parts[2]);
			
			if (parts.length == 4) {
					fix.setScores(parts[3]);
			}

			this.fixtures.add(fix);
		}
		return this;
	}
	
	public List<String> getSquad() {
		return squad;
	}
	
	public Competition setSquad(List<String> squad) {
		this.squad = squad;
		return this;
	}
		
	public Competition setSquad(String str) {
		this.squad = new ArrayList<String>();
		for (String line : str.split("[\\n\\r]")) {
			this.squad.add(line.trim());
		}
		return this;
	}

	public Item getItem() {
		return item;
	}

	public Competition setItem(Item i) {
		this.item = i;
		setName(i.getFieldValue(FieldName.TITLE));
		setSquad(i.getFieldValue(FieldName.SQUAD));
		setFixtures(i.getFieldValue(FieldName.FIXTURES));
		return this;
	}		
}
