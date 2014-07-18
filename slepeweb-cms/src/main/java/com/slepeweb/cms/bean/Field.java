package com.slepeweb.cms.bean;


public class Field {
	private Item item;
	private Integer fieldOrder;
	private String name, value;
	private FieldType type;
	
	public enum FieldType {
		string, integer, date, url;
	}
}
