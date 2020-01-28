package com.slepeweb.site.anc.bean.svg;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.site.anc.bean.Person;
import com.slepeweb.site.anc.bean.Relationship;

public class SvgSupport {
	
	private Link father, mother, subject, partner;
	private List<Link> children;
	private List<LineSegment> lineA, lineB, lineC;
	private Coord grandparentsIcon, parentsIcon, childrenIcon;
	private int frameHeight;
	
	public SvgSupport(Person pers, int relationshipId) {
		this.children = new ArrayList<Link>();
		this.lineA = new ArrayList<LineSegment>();
		this.lineB = new ArrayList<LineSegment>();
		this.lineC = new ArrayList<LineSegment>();
		
		// See hand-written notes for legend
		int p = 10, q = p + 100, r = q + 70;
		int a = 15, b = 160, c = 50;
		int u = 15;
		int w = 45, t = 60;
		
		Relationship subjectRel = pers.getRelationship(relationshipId);
		Relationship parentRel = new Relationship(pers.getFather(), pers.getMother());
		
		int numChildren = subjectRel != null ? subjectRel.getChildren().size() - 1 : 0;
		this.frameHeight = r + t + a + (numChildren * w);
		this.subject = new Link(c, q, pers);
		
		if (pers.getFather() != null) {
			this.father = new Link(0, p, pers.getFather());
		}
		
		if (pers.getMother() != null) {
			this.mother = new Link(b, p, pers.getMother());
		}
		
		if (subjectRel != null) {
			this.partner = new Link(c + b, q, subjectRel.getPartner());
		}
		
		if (subjectRel != null) {
			int num = 0, X = 2 * (u + c) + 5, Y;
			
			for (Person child : subjectRel.getChildren()) {
				num++;
				Y = num == 1 ? r + t : r + t + (num - 1) * w;
				children.add(new Link(X, Y, child));
			}
		}
		
		this.lineA = move(treeStyleA(a, b, c, parentRel, false), new Coord(u, p + t));
		this.lineB = move(treeStyleA(a, b, c, subjectRel, true), new Coord(u + c, q + t));
		this.lineC = move(treeStyleB(a, w, q + t, r + t, subjectRel), new Coord(2 * c + u, q + t + a));
		
		int dy = -10;
		this.grandparentsIcon = new Coord(300, p + dy);
		this.parentsIcon = new Coord(300, q + dy);
		this.childrenIcon = new Coord(300, r + (numChildren * w / 2));
	}
	
	private List<LineSegment> treeStyleA(int a, int b, int c, Relationship rel, boolean forSubject) {
		
		List<LineSegment> polyline = new ArrayList<LineSegment>();
		
		if (rel == null ||
				(rel.getSubject() == null && rel.getPartner() == null) || 
				(forSubject && rel.getPartner() == null && rel.getChildren().size() == 0)) {
			
			return polyline;
		}
		
		LineSegment horizontal = new LineSegment(new Coord(c, 0), new Coord(c, 0)).move(new Coord(0, a));
		LineSegment verticalShortLeft = new LineSegment(new Coord(0, a), new Coord(0, 0, true));
		LineSegment verticalShortRight = verticalShortLeft.copy().move(new Coord(b, 0));
		LineSegment verticalShortMiddle = verticalShortLeft.copy().move(new Coord(c, 0)).mirrorX(a);
		verticalShortMiddle.getEnd().setMarker(false);
		
		polyline.add(horizontal);
		
		if (rel.getSubject() != null) {
			polyline.add(verticalShortLeft);
			horizontal.copyStart(verticalShortLeft.getStart());
		}
			
		if (rel.getPartner() != null) {
			polyline.add(verticalShortRight);
			horizontal.copyEnd(verticalShortRight.getStart());
		}
		
		if ((rel.getSubject() != null || rel.getPartner() != null) && ! forSubject) {
			polyline.add(verticalShortMiddle);
		}
		
		return polyline;
	}
	
	private List<LineSegment> treeStyleB(int a, int w, int y, int z, Relationship rel) {
		
		List<LineSegment> polyline = new ArrayList<LineSegment>();
		
		if (rel == null) {
			return polyline;
		}
		
		int numChildren = rel.getChildren().size();
		
		if (numChildren == 0) {
			return polyline;
		}
		
		LineSegment vertical = new LineSegment(new Coord(0, z - (y + a) + (numChildren - 1) * w), new Coord(0, 0));		
		LineSegment horizontalShort = new LineSegment(new Coord(0, z - y - a), new Coord(a, z - y - a));
		
		polyline.add(vertical);
		polyline.add(horizontalShort);
		
		for (int i = 1; i < numChildren; i++) {
			polyline.add(horizontalShort.copy().move(new Coord(0, i * w)));
		}
		
		return polyline;
	}
	
	public List<LineSegment> copy(List<LineSegment> polyline) {
		List<LineSegment> copy = new ArrayList<LineSegment>();

		for (LineSegment seg : polyline) {
			copy.add(seg.copy());
		}
		
		return copy;
	}
	
	public List<LineSegment> move(List<LineSegment> polyline, Coord shift) {
		for (LineSegment seg : polyline) {
			seg.move(shift);
		}
		
		return polyline;
	}
	
	public Link getFather() {
		return father;
	}
	
	public Link getMother() {
		return mother;
	}
	
	public Link getSubject() {
		return subject;
	}
	
	public Link getPartner() {
		return partner;
	}
	
	public List<Link> getChildren() {
		return children;
	}
	
	public List<LineSegment> getLineA() {
		return lineA;
	}
	
	public List<LineSegment> getLineB() {
		return lineB;
	}
	
	public List<LineSegment> getLineC() {
		return lineC;
	}

	public Coord getGrandparentsIcon() {
		return grandparentsIcon;
	}

	public Coord getParentsIcon() {
		return parentsIcon;
	}

	public Coord getChildrenIcon() {
		return childrenIcon;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

}
