package com.slepeweb.cms.bean;


public class Field {
	private Long id;
	private Item item;
	private Integer fieldOrder;
	private String name;
	private FieldType type;
	
	public enum FieldType {
		string, integer, date, url;
	}
}
