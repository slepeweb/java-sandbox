package com.slepeweb.site.ntc.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.slepeweb.site.util.StringUtil;

public class Fixture {
	private static Logger LOG = Logger.getLogger(Fixture.class);
	private static String NTC = "Needingworth";
	
	private Competition competition;
	private Date date;
	private boolean home;
	private String opponent;
	private Integer scoreFor, scoreAgainst;
	
	public String getResult() {
		if (getScoreFor() != null) {
			return isHome() ? 
					String.format("%s (%d) vs. %s (%d)", NTC, getScoreFor(), getOpponent(), getScoreAgainst()) :
						String.format("%s (%d) vs. %s (%d)", getOpponent(), getScoreAgainst(), NTC, getScoreFor());
		}
		
		return isHome() ? 
				String.format("%s vs. %s", NTC, getOpponent()) :
					String.format("%s vs. %s", getOpponent(), NTC);
	}
	
	@Override
	public String toString() {		
		return String.format("%s: %s", getCompetition().getName(), getResult());
	}
	
	public Date getDate() {
		return date;
	}
	
	public Fixture setDate(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
		
		try {
			this.date = sdf.parse(s);
		}
		catch (Exception e) {
			LOG.error(String.format("Date parsing error [%s]", s));
		}
		
		return this;
	}
	
	public Fixture setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public boolean isHome() {
		return home;
	}
	
	public Fixture setHome(boolean home) {
		this.home = home;
		return this;
	}
	
	public Fixture setHome(String s) {
		this.home = s != null && s.equalsIgnoreCase("h");
		return this;
	}
	
	public String getOpponent() {
		return opponent;
	}
	
	public Fixture setOpponent(String opponent) {
		this.opponent = opponent;
		return this;
	}
	
	public Integer getScoreFor() {
		return scoreFor;
	}
	
	public Fixture setScoreFor(Integer scoreFor) {
		this.scoreFor = scoreFor;
		return this;
	}
	
	public Integer getScoreAgainst() {
		return scoreAgainst;
	}
	
	public Fixture setScoreAgainst(Integer scoreAgainst) {
		this.scoreAgainst = scoreAgainst;
		return this;
	}
	
	public Fixture setScores(String s) {
		String[] parts = StringUtil.splitLineIntoParts(s, "[-]", 2);
		if (parts != null && StringUtils.isNumeric(parts[0]) && StringUtils.isNumeric(parts[1])) {
			this.scoreFor = Integer.valueOf(parts[0]);
			this.scoreAgainst = Integer.valueOf(parts[1]);
		}
		return this;
	}

	public Competition getCompetition() {
		return competition;
	}

	public Fixture setCompetition(Competition competition) {
		this.competition = competition;
		return this;
	}
}
