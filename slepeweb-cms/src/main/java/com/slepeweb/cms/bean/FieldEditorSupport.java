package com.slepeweb.cms.bean;

public class FieldEditorSupport {

	private Field field;
	private FieldValue fieldValue;
	private String label, inputTag, validationRegexp;
	
	public Field getField() {
		return field;
	}
	
	public FieldEditorSupport setField(Field field) {
		this.field = field;
		return this;
	}
	
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
	
	public String getInputTag() {
		return inputTag;
	}
	
	public FieldEditorSupport setInputTag(String inputTag) {
		this.inputTag = inputTag;
		return this;
	}

	public String getValidationRegexp() {
		return validationRegexp;
	}

	public FieldEditorSupport setValidationRegexp(String validationRegexp) {
		this.validationRegexp = validationRegexp;
		return this;
	}
}
