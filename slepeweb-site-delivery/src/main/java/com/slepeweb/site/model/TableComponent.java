package com.slepeweb.site.model;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.constant.ItemTypeName;

public class TableComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	
	private List<Integer> columnWidths;
	private List<String> columnHeadings;
	private List<List<String>> rows;
	private int numColumns;

	public TableComponent setup(Link l) {
		super.setup(l);
		
		this.columnHeadings = new ArrayList<String>();
		this.columnWidths = new ArrayList<Integer>();
		this.rows = new ArrayList<List<String>>();
		
		Item i = l.getChild();
		
		for (String s : i.getFieldValue("colheadings").split("[|]")) {
			this.columnHeadings.add(s.trim());
		}
		
		for (String s : i.getFieldValue("colwidths").split("[|]")) {
			this.columnWidths.add(Integer.valueOf(s.trim()));
		}
		
		this.numColumns = this.columnHeadings.size();
		
		List<String> cells;
		for (Item c : i.getBoundItems()) {
			if (c.getType().getName().equals(ItemTypeName.TABLE_ROW)) {
				cells = new ArrayList<String>();
				for (int n = 1; n <= 6; n++) {
					cells.add(c.getFieldValue(String.format("table_cell_%d", n)));
				}
				
				this.rows.add(cells);
			}
		}
		
		return this;
	}
	
	public String toString() {
		return String.format("TableComponent (%s): %s", getType(), getHeading());
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Integer> getColumnWidths() {
		return columnWidths;
	}

	public List<String> getColumnHeadings() {
		return columnHeadings;
	}

	public List<List<String>> getRows() {
		return rows;
	}

	public int getNumColumns() {
		return numColumns;
	}

}
