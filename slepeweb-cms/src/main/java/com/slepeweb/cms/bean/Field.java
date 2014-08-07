package com.slepeweb.cms.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.slepeweb.cms.utils.LogUtil;


public class Field extends CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(Field.class);
	private Long id;
	private String name, variable, help;
	private FieldType type;
	private int size;
	private String defaultValue;
	
	public enum FieldType {
		text, markup, integer, date, url;
	}

	public void assimilate(Field f) {
		setName(f.getName());
		setVariable(f.getVariable());
		setHelp(f.getHelp());
		setType(f.getType());
		setSize(f.getSize());
	}

	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getVariable()) &&
			getType() != null;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", getVariable(), getType().name());
	}
	
	public Field save() {
		return getFieldService().save(this);
	}
	
	public void delete() {
		getFieldService().deleteField(this);
	}
	
	public Long getId() {
		return id;
	}

	public Field setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Field setName(String name) {
		this.name = name;
		return this;
	}

	public String getVariable() {
		return variable;
	}

	public Field setVariable(String variable) {
		this.variable = variable;
		return this;
	}

	public String getHelp() {
		return help;
	}

	public Field setHelp(String help) {
		this.help = help;
		return this;
	}

	public FieldType getType() {
		return type;
	}

	public Field setType(FieldType type) {
		this.type = type;
		return this;
	}

	public int getSize() {
		return size;
	}

	public Field setSize(int size) {
		this.size = size;
		return this;
	}

	public Object getDefaultValue() {
		if (getType() == FieldType.integer) {
			return StringUtils.isNotBlank(this.defaultValue) ? Integer.valueOf(this.defaultValue) : 0;
		}
		else if (getType() == FieldType.date) {
			// Timestamp format example: 2014-08-04 14:20:07.0
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			
			if (StringUtils.isNotBlank(this.defaultValue)) {
				try {
					return new Timestamp(sdf.parse(this.defaultValue).getTime());
				} catch (ParseException e) {
					LOG.warn(LogUtil.compose("Default date not parseable", this.defaultValue));
				}
			}
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, 10);
			return new Timestamp(cal.getTimeInMillis());
		}
		else {
			return StringUtils.isNotBlank(this.defaultValue) ? this.defaultValue : "";
		}
	}

	public Field setDefaultValue(Object value) {
		if (value instanceof Integer && getType() == FieldType.integer) {
			this.defaultValue = String.valueOf(value);
		}
		else if (value instanceof Timestamp && getType() == FieldType.date) {
			// TODO: format the date to 0 millis
			this.defaultValue = ((Timestamp) value).toString();
		}
		else {
			// TODO: May needs codes to indicate eg. 2 weeks hence (for dates), eg now, +2w, etc
			this.defaultValue = value != null ? value.toString() : "";
		}
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((help == null) ? 0 : help.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + size;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((variable == null) ? 0 : variable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (help == null) {
			if (other.help != null)
				return false;
		} else if (!help.equals(other.help))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (size != other.size)
			return false;
		if (type != other.type)
			return false;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}

}
