package com.slepeweb.cms.bean;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.slepeweb.cms.bean.guidance.IGuidance;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.common.util.DateUtil;


public class Field extends CmsBean {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(Field.class);
	private static final String INPUT_TAG = "input";
	private static final String TEXT_AREA_TAG = "textarea";
	private static final String SELECT_TAG = "select";
	private static final String RADIO = "radio";
	private static final String CHECKBOX = "checkbox";
	private static final String TEXT = "text";
	
	private Long id;
	private String name, variable, help;
	private FieldType type;
	private int size;
	private String defaultValue;
	private String validValues; 
	private boolean multilingual;
	
	public enum FieldType {
		text, markup, integer, date, datetime, dateish, url, radio, checkbox, select, layout;
	}

	public void assimilate(Object obj) {
		if (obj instanceof Field) {
			Field f = (Field) obj;
			setName(f.getName());
			setVariable(f.getVariable());
			setHelp(f.getHelp());
			setType(f.getType());
			setSize(f.getSize());
			setDefaultValue(f.getDefaultValue());
			setValidValues(f.getValidValues());
			setMultilingual(f.isMultilingual());
		}
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
	
	public String getInputTag(IGuidance guidance) {
		return getInputTag(null, guidance);
	}
	
	public String getInputTag(FieldValue fv, IGuidance guidance) {
		StringBuilder sb = new StringBuilder();
		String tag = null, inputType = "";
		String rows = null, cols = null;
		
		if (
				getType() == FieldType.integer || 
				getType() == FieldType.url || 
				getType() == FieldType.date|| 
				getType() == FieldType.datetime) {
			
			tag = INPUT_TAG;
			rows = cols = null;
			
			if (
					getType() == FieldType.date || 
					getType() == FieldType.datetime) {
						
				inputType = getType().name();
			}
		}
		else if (
				getType() == FieldType.text || 
				getType() == FieldType.markup || 
				getType() == FieldType.dateish) {
			
			if (getSize() > 0 && getSize() <= 120) {
				tag = INPUT_TAG;
				inputType = TEXT;
			}
			else {
				tag = TEXT_AREA_TAG;
				cols = "80";
				rows = getSize() > 0 && getSize() <= 256 ? "4" : "10";
			}
		}
		else if (getType() == FieldType.radio || getType() == FieldType.checkbox) {
			tag = INPUT_TAG;
			inputType = getType().name();
		}
		else if (getType() == FieldType.select) {
			tag = SELECT_TAG;
		}
		
		ValidValueList vvl = getValidValueListObject();
		String notNullStringValue = fv != null && fv.getStringValue() != null ? fv.getStringValue() : "";
		
		if (tag.equals(INPUT_TAG)) {
			// We need to produce an <input> element
			if (inputType.equals(RADIO) || inputType.equals(CHECKBOX)) {
				for (String vv : vvl.getValues()) {
					sb.append("<").append(tag).append(String.format(" type=\"%s\" name=\"%s\" value=\"%s\"%s ", 
							inputType, getVariable(), vv, getTooltip()));
					
					if (fv != null && fv.getStringValue() != null) {
						for (String partValue : fv.getStringValue().split("\\|")) {
							if (partValue.equals(vv)) {
								sb.append(" checked"); 
							}
						}
					}
					else if (getValidValueListObject().getDefaultValue().equals(vv)) {
						sb.append(" checked"); 
					}
					
					sb.append(String.format("/><span style=\"margin-right: 30px\">%s</span>", vv));
				}
			}
			else {
				if (inputType.equals(FieldType.date.name()) || inputType.equals(FieldType.datetime.name())) {
					Date d = null;
					String dateValueStr = "", timeValueStr = "";
					if (fv != null) {
						d = fv.getDateValue();
						dateValueStr = DateUtil.DATE_PATTERN_B.format(d);
						timeValueStr = DateUtil.TIME_PATTERN.format(d);
					}
					
					// Input field for the datepicker
					sb.append("<").append(tag).append(String.format(" type=\"text\" name=\"%s_d\" class=\"datepicker\" value=\"%s\"%s />", 
							getVariable(), dateValueStr, getTooltip()));
				
					if (inputType.equals(FieldType.datetime.name())) {
						// Input field for time
						sb.append("<").append(tag).append(String.format(" type=\"text\" name=\"%s_t\" class=\"timepicker\" value=\"%s\"%s />", 
								getVariable(), timeValueStr, getTooltip()));
					}
				}
				else {
					// This is a plain text input field, and NOT a date/datetime one
					sb.append("<").append(tag).append(String.format(" type=\"%s\" name=\"%s\" value=\"%s\"%s ", 
							inputType, getVariable(), notNullStringValue, getTooltip()));
					
					// Is there guidance for this field?
					if (guidance != null) {
						sb.append(String.format("data-validation=\"%s\" ", guidance.getRegExp()));
						sb.append(String.format("data-variable=\"%s\" ", getVariable()));
					}
					
					sb.append(" />");
				}
			}
		}
		else if (tag.equals(SELECT_TAG)) {
			sb.append("<").append(tag).append(String.format(" name=\"%s\" value=\"%s\"%s>", 
					getVariable(), notNullStringValue, getTooltip()));
			
			for (String vv : vvl.getValues()) {
				sb.append(String.format("<option value=\"%s\"%s>%s</option>", 
						vv, notNullStringValue.equals(vv) ? " selected" : "", vv));
			}
			sb.append("</").append(SELECT_TAG).append(">");
		}
		else if (tag.equals(TEXT_AREA_TAG)) {
			sb.append("<").append(tag).append(String.format(" name=\"%s\" cols=\"%s\" rows=\"%s\" spellcheck=\"%s\"%s>%s</%s>", 
					getVariable(), cols, rows, isMarkup() ? "false" : "true", getTooltip(), notNullStringValue, tag));
		}
		
		return sb.toString();
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

	public String getDefaultValue() {
		return this.defaultValue;
	}
	
	public Object getDefaultValueObject() {
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
			// Set default date to 7 days hence, in the absence of any other configuration rules.
			// A future date would be useful if we had embargoing functionality.
			// TODO: Implement rules for default dates, eg. '+7d' for 7 days hence
			cal.add(Calendar.DATE, 7);
			return new Timestamp(cal.getTimeInMillis());
		}
		else if (getType() == FieldType.radio || getType() == FieldType.checkbox || getType() == FieldType.select) {
			return getValidValueListObject().getDefaultValue();
		}
		else {
			return StringUtils.isNotBlank(this.defaultValue) ? this.defaultValue : "";
		}
	}
	
	public String getTooltip() {
		return StringUtils.isNotBlank(getHelp()) ? 
				String.format(" title=\"%s\"", getHelp().replaceAll("\"", "'").replaceAll("<", "")) : "";
	}

	public Field setDefaultValue(String value) {
		this.defaultValue = value;
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((validValues == null) ? 0 : validValues.hashCode());
		result = prime * result + ((help == null) ? 0 : help.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + size;
		result = prime * result + ((type == null) ? 0 : type.name().hashCode());
		result = prime * result + ((variable == null) ? 0 : variable.hashCode());
		result = prime * result + (multilingual ? 1231 : 1237);
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
		if (validValues == null) {
			if (other.validValues != null)
				return false;
		} else if (!validValues.equals(other.validValues))
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
		if (multilingual != other.multilingual)
			return false;
		return true;
	}

	public String getValidValues() {
		return validValues;
	}

	public Field setValidValues(String validValues) {
		this.validValues = validValues;
		return this;
	}

	public ValidValueList getValidValueListObject() {
		if (getValidValues() != null) { 
			ValidValueList vvl = new ValidValueList();
			boolean isFirstValue = true;
			for (String s : getValidValues().split("/ ")) {
				s = s.trim();
				if (s.endsWith("*")) {
					s = s.substring(0, s.length() - 1);
					vvl.setDefaultValue(s);
				}
				else if (isFirstValue) {
					vvl.setDefaultValue(s);
					isFirstValue = false;
				}
				vvl.addValue(s);
			}
			return vvl;
		}
		return null;
	}

	public boolean isMultilingual() {
		return multilingual;
	}

	public boolean isMarkup() {
		return this.type == FieldType.markup;
	}

	public Field setMultilingual(boolean multilingual) {
		this.multilingual = multilingual;
		return this;
	}

}
