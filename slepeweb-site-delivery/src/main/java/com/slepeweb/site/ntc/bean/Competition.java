package com.slepeweb.site.ntc.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.site.util.StringUtil;

public class Competition {

	private Item item;
	private String name, tableUrl, team;
	private Organiser organiser;
	private Integer tableId;
	private List<Fixture> fixtures;
	private List<String> squad;
	
	public enum Organiser {
		CAMBS, HUNTS
	}
	
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
	
	public List<Fixture> getFutureMatches() {
		List<Fixture> futureMatches = new ArrayList<Fixture>();
		Date now = new Date();
		
		for (Fixture f : getFixtures()) {
			if (f.getDate().after(now)) {
				futureMatches.add(f);
			}
		}
		
		return futureMatches;
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
		setTeam(i.getFieldValue(FieldName.TEAM));
		setTableUrl(i.getFieldValue(FieldName.URL));
		
		String[] data = i.getFieldValue(FieldName.DATA).split("[ ,]+");
		
		if (data.length > 0) {
			if (data[0].matches("cambs|hunts")) {
				setOrganiser(Organiser.valueOf(data[0].toUpperCase()));
			}
		}
		
		if (data.length > 1) {
			String str = data[1];
			if (StringUtils.isNumeric(str)) {
				setTableId(Integer.valueOf(str));
			}
		}

		return this;
	}

	public String getTeam() {
		return team;
	}

	public Competition setTeam(String team) {
		this.team = team;
		return this;
	}

	public Integer getTableId() {
		return this.tableId == null ? 0 : this.tableId;
	}

	public void setTableId(Integer tableIndex) {
		this.tableId = tableIndex;
	}	
	
	public Integer getOrganiserId() {
		return this.organiser.ordinal() + 1;
	}

	public Organiser getOrganiser() {
		return organiser;
	}

	public void setOrganiser(Organiser organiser) {
		this.organiser = organiser;
	}
	
	public String getOrganiserStr() {
		return this.organiser.name();
	}

}
