package com.slepeweb.site.anc.bean.svg;

import java.util.List;

import com.slepeweb.site.anc.bean.Person;

public class AncestryDiagram {
	public static final int MAX_LEVELS = 7;
	public static final int MIN_XOFFSET = 10;
	public static final int MIN_SPAN = 50;
	public static final int MIN_SEPERATOR = 20;
	public static final int[] NUM_COMPONENTS = new int[MAX_LEVELS];
	
	static {
		int level = 1;
		for (int i = 0; i < MAX_LEVELS; i++) {
			NUM_COMPONENTS[i] = level;
			level *= 2;
		}
	}
	
	private AncestorComponentRow[] rows = new AncestorComponentRow[MAX_LEVELS];
	private int numRows;
	private Hyperlink subject;
	private int width, height;

	public AncestryDiagram(Person subject) {
		this.subject = new Hyperlink(0, 0, subject);
	}
	
	public AncestorComponentRow[] getRows() {
		return rows;
	}
	
	public AncestorComponentRow getComponents(int level) {
		return this.rows[level];
	}
	
	public AncestryDiagram build(Person subject) {
		// Build the components
		String itemType = subject.getItem().getType().getName();
		if (! itemType.equals(Person.BOY) && ! itemType.equals(Person.GIRL)) {
			return null;
		}
		
		// Recursively climb the hiearchy
		build(0, new Person[] {subject});
		
		// Now position the components
		position();
		this.width = getMaxX() + 50;
		this.height = this.subject.getY() + 50;
		
		return this;
	}
	
	private void build(int level, Person[] subjects) {
		this.rows[level] = new AncestorComponentRow(subjects.length);
		AncestorComponent c;
		Person subject, parentA, parentB;
		Person[] parents = new Person[2 * subjects.length];
		int cursor = 0;
		boolean isEmptyRow = true;
		
		for (int i = 0; i < subjects.length; i++) {
			subject = subjects[i];
			
			if (subject != null) {
				parentA = subject.getFather();
				parentB = subject.getMother();
				c = new AncestorComponent(parentA, parentB);
				if (c.isBlank()) {
					c = null;
				}
				else {
					isEmptyRow = false;;
				}
			}
			else {
				c = null;
				parentA = parentB = null;
			}
			
			this.rows[level].setColumn(i, c);
			cursor = 2 * i;
			parents[cursor] = parentA;
			parents[cursor + 1] = parentB;
		}
		
		if (! isEmptyRow) {
			this.numRows = ++level;
			
			if (level < (MAX_LEVELS - 1)) {
				build(level, parents);
			}
		}
	}
	
	private void position() {
		AncestorComponent lastComponent = null;
		int diagramWidth = 1280;
		int 
			p = diagramWidth / 32, // left padding
			w = 2 * p, // companent span
			d = w; // component seperation
		
		int level = this.numRows - 1;
		int x = p, y = 100, rowPitch = 100;
		
		for (int rowId = level; rowId >= 0; rowId--) {
			x = p;
			
			for (AncestorComponent c : getRows()[rowId].getList()) {
				
				if (c != null) {
					c.build(w);
					c.offset(new Coord(x, y));
					lastComponent = c;
				}
				
				x += (w + d);
			}
			
			p *= 2;
			w *= 2;
			d *= 2;
			y += rowPitch;
		}
		
		// Finally, position the subject relative to the last component.
		// Need to identify the vertical tick at the bottom - usually the 
		// last segment.
		List<LineSegment> lastSet = lastComponent.getSegments();
		LineSegment target = lastSet.get(lastSet.size() - 1);		
		this.subject.move(target.getEnd().getX() - 20, target.getEnd().getY() + 10);
	}
	
	public int getNumRows() {
		return numRows;
	}

	public Hyperlink getSubject() {
		return subject;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public int getMaxX() {
		int max = 0, x;
		for (int i = 0; i < this.numRows; i++) {
			x = this.rows[i].getMaxX();
			max = x > max ? x : max;
		}
		return max;
	}

}
