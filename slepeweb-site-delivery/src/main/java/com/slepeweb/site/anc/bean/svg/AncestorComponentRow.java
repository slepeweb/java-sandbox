package com.slepeweb.site.anc.bean.svg;

public class AncestorComponentRow {

	private AncestorComponent[] list;
	private int size;
	
	public AncestorComponentRow(int size) {
		this.size = size;
		this.list = new AncestorComponent[size];
	}
	
	public AncestorComponent getColumn(int index) {
		if (index <= (this.size - 1)) {
			return this.list[index];
		}
		return null;
	}
	
	public void setColumn(int column, AncestorComponent comp) {
		this.list[column] = comp;
	}

	public AncestorComponent[] getList() {
		return list;
	}

	public int getSize() {
		return size;
	}

	public int getMaxX() {
		int max = 0, x;
		for (AncestorComponent c : this.list) {
			if (c != null) {
				x = c.getMaxX();
				max = x > max ? x : max;;
			}
		}
		return max;
	}
}
