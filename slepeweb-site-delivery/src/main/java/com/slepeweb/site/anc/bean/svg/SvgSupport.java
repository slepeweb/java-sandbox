package com.slepeweb.site.anc.bean.svg;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.site.anc.bean.Person;
import com.slepeweb.site.anc.bean.Relationship;

public class SvgSupport {
	
	private Hyperlink father, mother, subject, partner;
	private List<Hyperlink> children;
	private RelationshipBranch lineA, lineB;
	private List<LineSegment> lineC;
	private Coord grandparentsIcon, parentsIcon, childrenIcon;
	private int frameHeight;
	
	public SvgSupport(Person pers, int relationshipId) {
		this.children = new ArrayList<Hyperlink>();
		this.lineA = new RelationshipBranch();
		this.lineB = new RelationshipBranch();
		this.lineC = new ArrayList<LineSegment>();
		this.frameHeight = 200; // default value
				
		// See hand-written notes for legend
		int p = 10, q = p + 100, r = q + 70;
		int a = 15, b = 160, c = 50;
		int u = 15;
		int w = 45, t = 60;
		
		Relationship subjectRel = pers.getRelationship(relationshipId);
		Relationship parentRel = new Relationship(pers.getFather(), pers.getMother());
		boolean isParentDefined = parentRel.getSubject() != null || parentRel.getPartner() != null;
		
		// parentOffset is a vector pointing right and down when one of the parents exists.
		Coord parentsOffset = new Coord(0, 0);
		if (isParentDefined) {
			parentsOffset.move(c, q - p);
		}
		
		Coord fatherLinkDatum = new Coord(0, p);
		Coord motherLinkDatum = fatherLinkDatum.copy().move(b, 0);
		Coord subjectLinkDatum = fatherLinkDatum.copy().move(parentsOffset);
		Coord partnerLinkDatum = subjectLinkDatum.copy().move(b, 0);
		
		Coord lineAdatum = new Coord(u, p + t);
		Coord lineBdatum = lineAdatum.copy().move(parentsOffset);
		Coord lineCdatum = lineBdatum.copy().move(c, a);
		
		int numChildren = subjectRel != null ? subjectRel.getChildren().size() - 1 : 0;
		
		this.subject = new Hyperlink(subjectLinkDatum, pers);
		
		if (pers.getFather() != null) {
			this.father = new Hyperlink(fatherLinkDatum, pers.getFather());
		}
		
		if (pers.getMother() != null) {
			this.mother = new Hyperlink(motherLinkDatum, pers.getMother());
		}
		
		if (subjectRel != null) {
			this.partner = new Hyperlink(partnerLinkDatum, subjectRel.getPartner());
			Coord childLinkDatum = subjectLinkDatum.copy().move(90, 134);

			for (Person child : subjectRel.getChildren()) {
				children.add(new Hyperlink(childLinkDatum, child));
				childLinkDatum.move(0, w);
			}
			
			this.frameHeight = childLinkDatum.getY() - w + 10;
		}
		
		this.lineA = move(treeStyleA(a, b, c, parentRel, false), lineAdatum);
		this.lineB = move(treeStyleA(a, b, c, subjectRel, true), lineBdatum);
		this.lineC = move(treeStyleB(a, w, q + t, r + t, subjectRel), lineCdatum);
		
		int dy = -10;
		this.grandparentsIcon = new Coord(300, p + dy);
		this.parentsIcon = new Coord(300, q + dy);
		this.childrenIcon = new Coord(300, r + (numChildren * w / 2));
	}
	
	private RelationshipBranch treeStyleA(int a, int b, int c, Relationship rel, boolean forSubject) {
		
		RelationshipBranch polyline = new RelationshipBranch();
		
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
		
		if (rel.getSubject() != null && rel.getPartner() != null && StringUtils.isNotBlank(rel.getSummary())) {
			polyline.setSummary(rel.getSummary());
			polyline.setTooltipSegment(horizontal);
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
	
	public RelationshipBranch move(RelationshipBranch polyline, Coord shift) {
		for (LineSegment seg : polyline.getSegments()) {
			seg.move(shift);
		}
		
		return polyline;
	}
	
	public Hyperlink getFather() {
		return father;
	}
	
	public Hyperlink getMother() {
		return mother;
	}
	
	public Hyperlink getSubject() {
		return subject;
	}
	
	public Hyperlink getPartner() {
		return partner;
	}
	
	public List<Hyperlink> getChildren() {
		return children;
	}
	
	public RelationshipBranch getLineA() {
		return lineA;
	}
	
	public RelationshipBranch getLineB() {
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
