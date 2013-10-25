package com.slepeweb.sandbox.acm.navcache;

import java.util.regex.Pattern;

import com.mediasurface.client.IFieldDef;
import com.slepeweb.sandbox.acm.constants.FieldName;

interface ICacheableProperties {

	/**
	 * An item field value will be stored as a property of a NavigationLink UNLESS
	 * the field is on the PROPERTIES_NEVER_CACHED list, which includes:
	 * 		title, teaser, linktext, datepublished, hidefromnav, hidechildrenfromnav
	 * 
	 * Certain property values will ALWAYS be cached, regardless of whether the item 
	 * has a field with the same name. These properties will be on the 
	 * PROPERTIES_ALWAYS_CACHED list, eg. 'divider', 'share', etc.
	 * 
	 * Lastly, not all fields will be given the same treatment, and those fields  
	 * which require special treatment are declared in the PROPERTY_RULES map. 
	 * For example:
	 * - certain properties can never be null, so a default value is required
	 * - certain properties (eg. CSS_CLASS) cannot have a value of 'none', so mustn't
	 *   be stored at all
	 * - certain properties need to convert type from (say) String to Integer or Boolean
	 * - URL properties may need to be converted to relative URLs
	 * - etc.
	 * 
	 * Developer's may extend these this lists as necessary, ie:
	 * - PROPERTIES_NEVER_CACHED_ARR
	 * - PROPERTIES_ALWAYS_CACHED_ARR
	 * - PROPERTY_RULES_ARR
	 */

	static final String STRING = String.valueOf(IFieldDef.STRING_FIELD_TYPE);
	static final String INTEGER = String.valueOf(IFieldDef.INT_FIELD_TYPE);
	static final String DATE = String.valueOf(IFieldDef.DATE_FIELD_TYPE);
	static final String URL = String.valueOf(IFieldDef.URL_FIELD_TYPE);
	static final String BOOLEAN = "999";
	static final String STRING_TO_INTEGER = "998";
	static final String NEVER_NONE = "997";
	static final String RELATIVE_URL = "996";
	static final String NULL = null;
	static final String EMPTY = "";
	static final String NO = "no";
	static final String YES = "yes";
	static final Pattern DATE_PATTERN = Pattern.compile("(\\d\\d)-(\\d\\d)-(\\d\\d\\d\\d)");

	/* 
	 * These type-specific fields are NEVER cached as NavigationLink properties,
	 * either because they are already represented by NavigationLink member
	 * variables, or they potentially contain too much data, or they are
	 * unsuitable for any other reason.
	 */
	static final String[][] PROPERTIES_NEVER_CACHED_ARR = { { FieldName.DATE_PUBLISHED, "*" },
			{ FieldName.HIDE_CHILDREN_FROM_NAVIGATION, "*" }, { FieldName.HIDE_ITEM_FROM_NAVIGATION, "*" },
			{ FieldName.LINK_TEXT, "*" }, { FieldName.TITLE, "*" }, { FieldName.TEASER, "*" },
	};

	/* 
	 * These fields are always cached as NavigationLink properties, regardless of a)
	 * item type, and b) whether the corresponding item contains that field.
	 * 
	 * Since these fields will not always be present on all items, we can't determine the
	 * data type from the IType. Instead, item types need to be declared here.
	 */
	static final String[][] PROPERTIES_ALWAYS_CACHED_ARR = {};

	/* 
	 * Format is: <field-name>, <field-data-type>, <default-value>
	 * 
	 * These fields require special handling before their values are stored as
	 * NavigationLink properties.
	 */
	static final String[][] PROPERTY_RULES_ARR = {
			// Fields that can never be cached when the value is "none"
			//{ FieldNames.BACKGROUND, NEVER_NONE, NULL },

			// Field that have a default value
			//{ FieldNames.GALLERY_SHORTCUT, STRING, EMPTY },

			// String fields that need to be converted to Booleans
			//{ FieldNames.DEFAULT_TO_FIRST_CHILD, BOOLEAN, NO }, 

			// String fields that need to be converted to Integer values
			//{ FieldNames.NAV_LEVEL, STRING_TO_INTEGER, NULL },

			// URLs that need to be converted to relative URLs
			//{ FieldNames.GALLERY_ITEM_URL, RELATIVE_URL, EMPTY }, 
	};
}
