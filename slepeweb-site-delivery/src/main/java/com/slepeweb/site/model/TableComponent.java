package com.slepeweb.site.model;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;

public class TableComponent extends SimpleComponent {
	private static final long serialVersionUID = 1L;
	
	private ColumnHeading[] columnHeadings;
	private List<Row> rows;
	private int numColumns;

	public TableComponent setup(Link l) {
		super.setup(l);
		
		Item i = l.getChild();
		this.numColumns = i.getIntFieldValue("numcolumns");
		String[] labels = splitTrimAndFill(i.getFieldValue("colheadings"));
		String[] widths = splitTrimAndFill(i.getFieldValue("colwidths"));
		this.columnHeadings = buildColumnHeadings(labels, widths);
		this.rows = new ArrayList<Row>(this.numColumns);
		
		String data = i.getFieldValue("tabledata");
		
		try {
			BufferedReader r = new BufferedReader(new StringReader(data));
			String line;
			
			while ((line = r.readLine()) != null) {
				this.rows.add(new Row(splitTrimAndFill(line)));
			}
		}
		catch (Exception e) {
			System.err.print(e.getMessage());
		}
		
		return this;
	}
	
	private String[] splitTrimAndFill(String line) {
		String[] arr = line.split("[|]");
		String[] result = new String[this.numColumns];
		for (int i = 0; i < arr.length; i++) {
			if (i < this.numColumns) {
				result[i] = arr[i].trim();
			}
		}
		
		int diff = this.numColumns - arr.length;
		if (diff > 0) {
			for (int i = 1; i <= diff; i++) {
				result[arr.length + i - 1] = "";
			}
		}
		
		return result;
	}
	
	private ColumnHeading[] buildColumnHeadings(String[] labels, String[] widths) {
		ColumnHeading[] headings = new ColumnHeading[this.numColumns];
		for (int i = 0; i < this.numColumns; i++) {
			headings[i] = new ColumnHeading(labels[i], widths[i]);
		}
		return headings;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ColumnHeading[] getColumnHeadings() {
		return columnHeadings;
	}

	public List<Row> getRows() {
		return rows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public static class ColumnHeading {
		private String label;
		private int width;
		
		public ColumnHeading() {}
		
		public ColumnHeading(String label, String width) {
			this.label = label;
			this.width = ! StringUtils.isNumeric(width) ? 0 : Integer.valueOf(width);
		}
		
		public String getLabel() {
			return label;
		}
		
		public ColumnHeading setLabel(String label) {
			this.label = label;
			return this;
		}
		
		public int getWidth() {
			return width;
		}
		
		public ColumnHeading setWidth(int width) {
			this.width = width;
			return this;
		}	
	}
	
	
	public static class Row {
		private List<String> cells = new ArrayList<String>();
		
		public Row() {}
		
		public Row(String[] cells) {
			for (String s : cells) {
				this.cells.add(s);
			}
		}
		
		public List<String> getCells() {
			return cells;
		}
		
		public int getSize() {
			return this.cells.size();
		}
	}
}
