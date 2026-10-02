package com.slepeweb.cms.bean;

import java.util.Date;

import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.guidance.IGuidance;
import com.slepeweb.common.util.DateUtil;

public class FieldEditorSupport {

	private static final String INPUT_TAG = "input";
	private static final String TEXT_AREA_TAG = "textarea";
	private static final String SELECT_TAG = "select";
	private static final String RADIO = "radio";
	private static final String CHECKBOX = "checkbox";
	private static final String TEXT = "text";
	
	private FieldValue fieldValue;
	private String label, inputTag;
	private IGuidance guidance;
	
	public FieldValue getFieldValue() {
		return fieldValue;
	}
	
	public FieldEditorSupport setFieldValue(FieldValue fieldValue) {
		this.fieldValue = fieldValue;
		return this;
	}
	
	public String getLabel() {
		return label;
	}
	
	public FieldEditorSupport setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public IGuidance getGuidance() {
		return guidance;
	}

	public FieldEditorSupport setGuidance(IGuidance guidance) {
		this.guidance = guidance;
		return this;
	}
	
	public String getInputTag() {
		if (this.inputTag == null) {
			this.inputTag = buildInputTag();
		}
		return this.inputTag;
	}
	
	private String buildInputTag() {
		StringBuilder sb = new StringBuilder();
		String tagName = null, inputType = "";
		String rows = null, cols = null;
		Field f = this.fieldValue.getField();
		FieldType fieldType = f.getType();
		int fieldSize = f.getSize();
		
		if (
				fieldType == FieldType.integer || 
				fieldType == FieldType.url || 
				fieldType == FieldType.date|| 
				fieldType == FieldType.datetime) {
			
			tagName = INPUT_TAG;
			rows = cols = null;
			
			if (
					fieldType == FieldType.date || 
					fieldType == FieldType.datetime) {
						
				inputType = fieldType.name();
			}
		}
		else if (
				fieldType == FieldType.text || 
				fieldType == FieldType.markup || 
				fieldType == FieldType.dateish) {
			
			if (fieldSize > 0 && fieldSize <= 120) {
				tagName = INPUT_TAG;
				inputType = TEXT;
			}
			else {
				tagName = TEXT_AREA_TAG;
				cols = "80";
				rows = fieldSize > 0 && fieldSize <= 256 ? "4" : "10";
			}
		}
		else if (fieldType == FieldType.radio || fieldType == FieldType.checkbox) {
			tagName = INPUT_TAG;
			inputType = fieldType.name();
		}
		else if (fieldType == FieldType.select) {
			tagName = SELECT_TAG;
		}
		
		ValidValueList vvl = f.getValidValueListObject();
		String notNullStringValue = this.fieldValue != null && this.fieldValue.getStringValue() != null ? this.fieldValue.getStringValue() : "";
		
		// The browser will interfere with entity codes
		// notNullStringValue = notNullStringValue.replaceAll("&(.*?;)", "&amp;$1");
		
		if (tagName.equals(INPUT_TAG)) {
			// We need to produce an <input> element
			if (inputType.equals(RADIO) || inputType.equals(CHECKBOX)) {
				for (String vv : vvl.getValues()) {
					sb.append("<").append(tagName).append(String.format(" type=\"%s\" name=\"%s\" value=\"%s\"%s ", 
							inputType, f.getVariable(), vv, f.getTooltip()));
					
					if (this.fieldValue != null && this.fieldValue.getStringValue() != null) {
						for (String partValue : this.fieldValue.getStringValue().split("\\|")) {
							if (partValue.equals(vv)) {
								sb.append(" checked"); 
							}
						}
					}
					else if (f.getValidValueListObject().getDefaultValue().equals(vv)) {
						sb.append(" checked"); 
					}
					
					sb.append(String.format("/><span style=\"margin-right: 30px\">%s</span>", vv));
				}
				
				return sb.toString();
			}
			
			if (inputType.equals(FieldType.date.name()) || inputType.equals(FieldType.datetime.name())) {
				Date d = null;
				String dateValueStr = "", timeValueStr = "";
				if (this.fieldValue != null) {
					d = this.fieldValue.getDateValue();
					dateValueStr = DateUtil.DATE_PATTERN_B.format(d);
					timeValueStr = DateUtil.TIME_PATTERN.format(d);
				}
				
				// Input field for the datepicker
				sb.append("<").append(tagName).append(String.format(" type=\"text\" name=\"%s_d\" class=\"datepicker\" value=\"%s\"%s />", 
						f.getVariable(), dateValueStr, f.getTooltip()));
			
				if (inputType.equals(FieldType.datetime.name())) {
					// Input field for time
					sb.append("<").append(tagName).append(String.format(" type=\"text\" name=\"%s_t\" class=\"timepicker\" value=\"%s\"%s />", 
							f.getVariable(), timeValueStr, f.getTooltip()));
				}
				
				return sb.toString();
			}
			
			// This is a plain text input field, and NOT a date/datetime one
			sb.append(new StringBuilder(String.format("<%s type=\"%s\" name=\"%s\" value=\"%s\" %s ", 
					tagName, inputType, f.getVariable(), notNullStringValue, f.getTooltip())));
			
			// Is there guidance for this field?
			if (this.guidance != null) {
				sb.append(String.format("data-validation=\"%s\" data-variable=\"%s\" ", this.guidance.getRegExp(), f.getVariable()));
			}
			
			sb.append(String.format("value=\"%s\" />", notNullStringValue));			
			return sb.toString();
		}
		
		if (tagName.equals(SELECT_TAG)) {
			sb.append("<").append(tagName).append(String.format(" name=\"%s\" value=\"%s\"%s>", 
					f.getVariable(), notNullStringValue, f.getTooltip()));
			
			for (String vv : vvl.getValues()) {
				sb.append(String.format("<option value=\"%s\"%s>%s</option>", 
						vv, notNullStringValue.equals(vv) ? " selected" : "", vv));
			}
			
			sb.append("</").append(SELECT_TAG).append(">");
			return sb.toString();
		}
		
		if (tagName.equals(TEXT_AREA_TAG)) {
			sb.append("<").append(tagName).append(String.format(" name=\"%s\" cols=\"%s\" rows=\"%s\" spellcheck=\"%s\"%s>%s</%s>", 
					f.getVariable(), cols, rows, f.isMarkup() ? "false" : "true", f.getTooltip(), notNullStringValue, tagName));
			return sb.toString();
		}
		
		return "";
	}
	
}