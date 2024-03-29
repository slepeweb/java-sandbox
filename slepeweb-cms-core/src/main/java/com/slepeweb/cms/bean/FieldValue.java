package com.slepeweb.cms.bean;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.guidance.IGuidance;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.common.util.StringUtil;

public class FieldValue extends CmsBean {
	private static final long serialVersionUID = 1L;
	
	private static String LINK_PATTERN_STR = "\\$_(\\d+)";
	private static Pattern ANCHOR_PATTERN = 
			Pattern.compile(String.format("(<a href=\")%s(\".*?>)(.*?)(</a>)", LINK_PATTERN_STR), 
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static Pattern IMAGE_PATTERN = 
			Pattern.compile(String.format("(<img src=\")%s(\".*?>(.*?)>)", LINK_PATTERN_STR), 
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	
	private Long itemId;
	private Field field;
	private String stringValue, stringValueResolved;
	private Integer integerValue;
	private Timestamp dateValue;
	private String language = "en";
	
	public FieldValue shallowClone() {
		FieldValue fv = new FieldValue();
		fv.assimilate(this);
		fv.setCmsService(getCmsService());
		return fv;
	}
	
	public void assimilate(Object obj) {
		if (obj instanceof FieldValue) {
			FieldValue fv = (FieldValue) obj;
			setItemId(fv.getItemId());
			setField(fv.getField());
			setStringValue(fv.getStringValue());
			setIntegerValue(fv.getIntegerValue());
			setDateValue(fv.getDateValue());
			setLanguage(fv.getLanguage());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			getItemId() != null &&
			getField() != null && 
			getField().getId() != null &&
			StringUtils.isNotBlank(getLanguage());
	}
	
	public Long getId() {
		return NO_ID;
	}
	
	@Override
	public String toString() {
		return String.format("itemId=%d: %s (%s) {%s}", getItemId(), getField(), getLanguage(), 
				StringUtils.abbreviate(getStringValue(), 64));
	}
	
	public FieldValue save() throws ResourceException {
		return getFieldValueService().save(this);
	}
	
	public void delete() {
		getFieldValueService().deleteFieldValue(getField().getId(), getItemId(), getLanguage());
	}
	
	public String getInputTag(IGuidance guidance) {
		return getField().getInputTag(this, guidance);
	}
	
	public Field getField() {
		return field;
	}

	public FieldValue setField(Field field) {
		this.field = field;
		return this;
	}
	
	public FieldValue setValue(Object value) {
		if (value != null) {
			setStringValue(value.toString());

			if (value instanceof Integer) {
				setIntegerValue((Integer) value);
			}
			else if (value instanceof Timestamp) {
				setDateValue((Timestamp) value);
				setStringValue(getDateValue().toString());
			}
		}
		return this;
	}
	
	public String getStringValue() {
		return stringValue;
	}

	public FieldValue setStringValue(String stringValue) {
		this.stringValue = stringValue;
		return this;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public FieldValue setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
		return this;
	}

	public Timestamp getDateValue() {
		return dateValue;
	}

	public FieldValue setDateValue(Timestamp dateValue) {		
		if (dateValue != null) {
			this.dateValue = dateValue;
			this.dateValue.setNanos(0);
		}
		return this;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getItemId() {
		return itemId;
	}

	public FieldValue setItemId(Long itemId) {
		this.itemId = itemId;
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public FieldValue setLanguage(String language) {
		this.language = language;
		return this;
	}

	public String getStringValueResolved() {
		if (this.stringValueResolved == null) {
			if (this.field.getType() == FieldType.markup) {
				// Take this opportunity to wrap text nodes with para tags
				this.stringValueResolved = StringUtil.wrapWithMarkup(this.stringValue, "p", null);
				
				// Now do what this method was originally intended for
				this.stringValueResolved = resolveLinks(this.stringValueResolved);
			}
			else {
				this.stringValueResolved = this.stringValue;
			}
		}
		return this.stringValueResolved;
	}

	public void setStringValueResolved(String stringValueResolved) {
		this.stringValueResolved = stringValueResolved;
	}

	private String resolveLinks(String s) {
		if (s.indexOf("$_") > -1) {
			String replacement = resolveLinks(ANCHOR_PATTERN.matcher(s));
			return resolveLinks(IMAGE_PATTERN.matcher(replacement));
		}
		return s;
	}
	
	private String resolveLinks(Matcher m) {
		StringBuffer sb = new StringBuffer();
		String id;
		Item i;
		String r = null;

		while (m.find()) {
			id = m.group(2);
			i = getCmsService().getItemService().getItemByOriginalId(Long.parseLong(id));

			if (m.pattern().equals(ANCHOR_PATTERN)) {
				if (i != null) {
					// Replace link ref with item path
					r = m.group(1) + i.getUrl() + m.group(3) + m.group(4) + m.group(5);
				}
				else {
					// Remove surrounding <a> tag, and leave behind the body.
					r = m.group(4);
				}
			}
			else if (m.pattern().equals(IMAGE_PATTERN)) {
				if (i != null) {
					// Replace link ref with item path
					r = m.group(1) + i.getUrl() + m.group(3);
				}
				else {
					// Remove <img> tag.
					r = "";
				}
			}
			
			m.appendReplacement(sb, r);
		}
		
		m.appendTail(sb);
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateValue == null) ? 0 : dateValue.hashCode());
		result = prime * result + ((field == null) ? 0 : field.getId().hashCode());
		result = prime * result + ((integerValue == null) ? 0 : integerValue.hashCode());
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
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
		
		FieldValue other = (FieldValue) obj;
		if (dateValue == null) {
			if (other.dateValue != null)
				return false;
		} else if (!dateValue.equals(other.dateValue))
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.getId().equals(other.field.getId()))
			return false;
		if (integerValue == null) {
			if (other.integerValue != null)
				return false;
		} else if (!integerValue.equals(other.integerValue))
			return false;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		if (stringValue == null) {
			if (other.stringValue != null)
				return false;
		} else if (!stringValue.equals(other.stringValue))
			return false;
		
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		
		return true;
	}

}
