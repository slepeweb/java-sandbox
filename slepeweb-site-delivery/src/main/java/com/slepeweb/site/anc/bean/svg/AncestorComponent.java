package com.slepeweb.site.anc.bean.svg;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.site.anc.bean.Person;

public class AncestorComponent {

	private List<LineSegment> segments = new ArrayList<LineSegment>();
	private Hyperlink parentA, parentB;
	
	public AncestorComponent(Person a, Person b) {
		this.parentA = new Hyperlink(0, 0, a);
		this.parentB = new Hyperlink(0, 0, b);
	}
	
	public boolean isBlank() {
		return this.parentA.isBlank() && this.parentB.isBlank();
	}
	
	public AncestorComponent add(LineSegment seg) {
		getSegments().add(seg);
		return this;
	}
	
	public List<LineSegment> getSegments() {
		return segments;
	}
	
	public Hyperlink getParentA() {
		return parentA;
	}

	public Hyperlink getParentB() {
		return parentB;
	}

	/*
	 * Line segments are created to form the following:
	 * 
	 *  |__________________|
	 *            |
	 *  
	 *  The long horizontal line sits on the y=0 axis. The whole component
	 *  gets positioned later by the 'offset' method.
	 */
	public AncestorComponent build(int span) {
		LineSegment seg;
		int tick = 15;
		int middle = span / 2;
		
		if (! isBlank()) {
			// First, the horizontal line
			if (this.parentA.isBlank() || this.parentB.isBlank()) {
				// Only one parent identified - halve the length of the span
				span = middle;
			}
			
			seg = new LineSegment(new Coord(0, 0), new Coord(span, 0));
			if (this.parentA.isBlank()) {
				// Move the horizontal line to the right
				seg.move(new Coord(middle, 0));
			}
			this.segments.add(seg);
			
			// Next, the leftmost tick mark
			seg = new LineSegment(new Coord(0, 0), new Coord(0, -tick, true));
			if (! this.parentA.isBlank()) {
				this.segments.add(seg);
			}
			
			// Next, the rightmost tick mark
			if (! this.parentB.isBlank()) {
				this.segments.add(seg.copy().move(new Coord(span, 0)));
			}
			
			// And lastly, the bottom tick
			seg = seg.copy().move(new Coord(middle, 0)).mirrorX(0);
			seg.getEnd().setMarker(false);
			this.segments.add(seg);
			
			// Now adjust the positions of the person links
			int xOffset = -30;
			int yOffset = 2 * xOffset - 15;
			this.parentA.set(xOffset, yOffset);
			
			if (! this.parentB.isBlank()) {
				this.parentB.set(xOffset + span, yOffset);
			}
		}
		
		return this;
	}
	
	public AncestorComponent offset(Coord offs) {
		for (LineSegment seg : this.segments) {
			seg.move(offs);
		}
		
		this.parentA.move(offs);
		this.parentB.move(offs);
		
		return this;
	}
	
	public int getMaxX() {
		int max = 0, x;
		for (LineSegment seg : this.segments) {
			x = seg.getMaxX();
			max = x > max ? x : max;
		}
		return max;
	}
}
