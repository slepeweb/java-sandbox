package com.slepeweb.site.anc.bean.svg;

import java.util.ArrayList;
import java.util.List;

public class RelationshipBranch {

	private List<LineSegment> segments = new ArrayList<LineSegment>();
	private LineSegment tooltipSegment;
	private String summary;
	
	public boolean isEmpty() {
		return this.segments.size() == 0;
	}
	
	public RelationshipBranch add(LineSegment seg) {
		getSegments().add(seg);
		return this;
	}
	
	public List<LineSegment> getSegments() {
		return segments;
	}
	
	public RelationshipBranch setSegments(List<LineSegment> segments) {
		this.segments = segments;
		return this;
	}
	
	public String getSummary() {
		return this.summary;
	}
	
	public String getTooltip() {
		return getSummary();
	}
	
	public RelationshipBranch setSummary(String s) {
		this.summary = s;
		return this;
	}

	public Coord getTooltipPos() {
		if (this.tooltipSegment != null) {
			return this.tooltipSegment.getTextDatum(60);
		}
		return null;
	}

	public LineSegment getTooltipSegment() {
		return tooltipSegment;
	}

	public RelationshipBranch setTooltipSegment(LineSegment tooltipSegment) {
		this.tooltipSegment = tooltipSegment;
		return this;
	}

}
