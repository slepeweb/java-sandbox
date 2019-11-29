package com.slepeweb.ifttt.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonObj {

	private String name;
	private Object value;
	private List<JsonObj> list;
	private Type type;
	public enum Type {STRING, INTEGER, LIST, STRUC};
	
	public static JsonObj createStruc() {
		return new JsonObj(new HashMap<String, JsonObj>());
	}
	
	public static JsonObj createList() {
		return new JsonObj(new ArrayList<JsonObj>());
	}
	
	public static JsonObj create(Object o) {
		if (o instanceof String) {
			return new JsonObj((String) o);
		}
		else if (o instanceof Integer) {
			return new JsonObj((Integer) o);
		}
		return new JsonObj("null_string");
	}
	
	public JsonObj(String s) {
		this.value = s;
		this.type = Type.STRING;
	}
	
	public JsonObj(Integer i) {
		this.value = i;
		this.type = Type.INTEGER;
	}
	
	public JsonObj(List<JsonObj> l) {
		this.value = l;
		this.type = Type.LIST;
	}
	
	public JsonObj(Map<String, JsonObj> m) {
		this.value = m;
		this.type = Type.STRUC;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, JsonObj> getStruc() {
		if (this.type == Type.STRUC) {
			return (Map<String, JsonObj>) this.value;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<JsonObj> getListValue() {
		if (this.type == Type.LIST) {
			return (List<JsonObj>) this.value;
		}
		return null;
	}
	
	public JsonObj put(String key, JsonObj val) {
		Map<String, JsonObj> m = getStruc();
		if (m != null) {
			m.put(key, val);
			val.setName(key);
		}
		return this;
	}
	
	public JsonObj add(JsonObj val) {
		List<JsonObj> l = getListValue();
		if (l != null) {
			l.add(val);
		}
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("(%s) ", getType().name()));
		if (isCollection()) {
			sb.append(getName());
		}
		else if (getType() == Type.STRING) {
			sb.append(String.format("\"%s\"", (String) getValue()));
		}
		else if (getType() == Type.INTEGER) {
			sb.append(getValue());
		}
		
		return sb.toString();
	}
	
	public boolean isCollection() {
		return getType() == Type.STRUC || getType() == Type.LIST;
	}
	
	public JsonObj find(String nameIdentifiers) {
		JsonObj it = this;
		
		for (String name : nameIdentifiers.split("\\.")) {
			if (it == null) {
				break;
			}
			
			it = find(it, name);
		}
		
		return it;
	}
	
	private JsonObj find(JsonObj it, String name) {
		JsonObj target = null;
		
		if (it.getType() == Type.STRUC) {
			// Examine the properties in this object
			Map<String, JsonObj> map = it.getStruc();
			if (map.containsKey(name)) {
				target = map.get(name);
			}
			else {
				// Examine all the map values, looking for nested objects
				for (Map.Entry<String, JsonObj> e : map.entrySet()) {
					if (e.getValue().isCollection()) {
						target = e.getValue().find(name);
						if (target != null) {
							break;
						}
					}
				}
			}
		}
		else if (getType() == Type.LIST) {
			// Examine each member of the list, looking for objects
			for (JsonObj o : getList()) {
				if (o.isCollection()) {
					target = o.find(name);
					if (target != null) {
						break;
					}
				}
			}
		}
		
		return target;
	}
	
	public int size() {
		if (! isCollection()) {
			return 1;
		}
		else {
			if (getType() == Type.STRUC) {
				return getStruc().size();
			}
			else if (getType() == Type.LIST) {
				return getList().size();
			}
		}
		
		return -1;
	}
	
	public String stringify() {
		if (this.type == Type.STRING) {
			return String.format("\"%s\"", this.value.toString());
		}
		else if (this.type == Type.STRUC) {
			StringBuilder sb = new StringBuilder();
			@SuppressWarnings("unchecked")
			Map<String, JsonObj> m = (Map<String, JsonObj>) this.value;
			int index = 0;
			for (Map.Entry<String, JsonObj> o : m.entrySet()) {
				if (index++ > 0) {
					sb.append(", ");
				}
				sb.append(String.format("\"%s\": %s", o.getKey(), o.getValue().stringify()));
			}
			
			return String.format("{%s}", sb.toString());
		}
		else if (this.type == Type.LIST) {
			StringBuilder sb = new StringBuilder();
			@SuppressWarnings("unchecked")
			List<JsonObj> list = (List<JsonObj>) this.value;
			int index = 0;
			for (JsonObj o : list) {
				if (index++ > 0) {
					sb.append(", ");
				}
				sb.append(o.stringify());
			}
			
			return String.format("[%s]", sb.toString());
		}
		else if (this.type == Type.INTEGER) {
			return String.format("%s", this.value.toString());
		}
		
		return null;
	}
	
	public Object getValue() {
		return value;
	}
	
	public List<JsonObj> getList() {
		return list;
	}
	
	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
