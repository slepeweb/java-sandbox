package com.slepeweb.site.anc.bean.svg;

public class LineSegment {

	private Coord start, end;
	
	public LineSegment(Coord a, Coord b) {
		this.start = a;
		this.end = b;
	}
	
	@Override
	public String toString() {
		return String.format("From %s, to %s", getStart(), getEnd());
	}
	
	public LineSegment copy() {
		return new LineSegment(this.start.copy(), this.end.copy());
	}
	
	public void copyStart(Coord c) {
		this.start.setX(c.getX());
		this.start.setY(c.getY());
	}
	
	public void copyEnd(Coord c) {
		this.end.setX(c.getX());
		this.end.setY(c.getY());
	}
	
	public LineSegment move(Coord shift) {
		this.start.move(shift);
		this.end.move(shift);
		return this;
	}

	public LineSegment mirrorX(int mirrorPlaneY) {
		this.start.moveY(2 * (mirrorPlaneY - this.start.getY()));
		this.end.moveY(2 * (mirrorPlaneY - this.end.getY()));
		return this;
	}

	public Coord getStart() {
		return start;
	}

	public Coord getEnd() {
		return end;
	}

	public LineSegment setStart(Coord start) {
		this.start = start;
		return this;
	}

	public LineSegment setEnd(Coord end) {
		this.end = end;
		return this;
	}

	public Coord getTextDatum(int textPosPercent) {
		int dx = (getEnd().getX() - getStart().getX()) * textPosPercent / 100;
		int dy = (getEnd().getY() - getStart().getY()) * textPosPercent / 100;
		return new Coord(getStart().copy().move(new Coord(dx, dy)));
	}

}
