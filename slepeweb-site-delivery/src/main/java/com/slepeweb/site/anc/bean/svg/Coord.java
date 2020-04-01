package com.slepeweb.site.anc.bean.svg;

public class Coord {
	
	private int x, y;
	private boolean marker;
	
	public Coord(int a, int b) {
		this(a, b, false);
	}
	public Coord(Coord source) {
		this(source.getX(), source.getY(), source.isMarker());
	}

	public Coord(int a, int b, boolean marker) {
		this.x = a;
		this.y = b;
		this.marker = marker;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", getX(), getY());
	}
	
	public Coord copy() {
		return new Coord(this);
	}
	
	public Coord move(Coord shift) {
		moveX(shift.getX());
		moveY(shift.getY());
		return this;
	}

	public Coord moveX(int delta) {
		this.x += delta;
		return this;
	}

	public Coord moveY(int delta) {
		this.y += delta;
		return this;
	}

	public Coord move(int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Coord back(Coord shift) {
		this.x -= shift.getX();
		this.y -= shift.getY();
		return this;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isMarker() {
		return marker;
	}

	public void setMarker(boolean marker) {
		this.marker = marker;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
}
