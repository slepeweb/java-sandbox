/**
 ***********************************************************************
 ***********************************************************************
Copyright (C) 1996 - 2011 Alterian Technology Ltd. All rights reserved.

Alterian Technology Ltd
Alterian plc
The Spectrum Building
Bond Street
Bristol BS1 3LG, UK
+44 (0) 117 970 3200 +44 (0) 117 970 3201

http://www.alterian.com
info@alterian.com
 ***********************************************************************
File Information
================

%PID% %PRT% %PO%
%PM% - %PD%

 ***********************************************************************
 ***********************************************************************
 */

//Key on left, locale string on right.
//Text within the mark up #{} ( e.g. #{width} ) are format strings and should not be translated.
var PSLanguage_en = {
	"Select Component": "Select Component",
	"Select_component_tooltip_text": "Using the <em>Select Component</em> tool, you can search for and add components such as item fields and images to the page.",
	"Cell Properties": "Cell Properties",
	"Cell_properties_tooltip_text": "Using the <em>Cell Properties</em> tool, you can change the width and style of a cell.",
	"Add Component": "Add Component",
	"Change Layout": "Manage Layout",
	"Change_layout_tooltip_text": "Using the <em>Manage Layout</em> tool, you can choose from any available ready made layouts and add them to the page or save the current page layout so it can be reused.",
	"add_layout": "Add Layout",
	"Save Master": "Save as Master Layout",
	"Save_master_tooltip_text": "Saving a <em>Master Layout</em> will set the current page layout as the default layout for the current template. If an item does not have its own layout, it will use the template default.",
	"Delete Cell": "Delete Cell",
	"Delete Item": "Delete Item",
	"Filter": "Filter",
	"Height": "Height",
	"Width": "Width",
	"H_height": "H",
	"W_width": "W",
	"all": "all",
	"category": "category",
	"All": "All",
	"Field": "Field",
	"Image": "Image",
	"None": "None",
	"OK": "OK",
	"Cancel": "Cancel",
	"Submit component configuration": "Submit component configuration",
	"Close component configuration without submitting": "Close component configuration without submitting",
	"Display Component Configuration": "Display Component Configuration",
	"component_configuration_error": "There was a problem saving your component configuration.",
	"Details": "Details",
	"Reason": "Reason",
	"confirm_blank_layout": "This layout contains no cells. If you continue, any cell items in your current layout will be removed.",
	"image_already_exists": "This image is already an inline for this item.",
	"Properties": "Properties",
	"change_layout_instructions": "Select the layout you wish to use.",
	"create_layout_instructions": "Preview and name the layout before saving.",
	"cell_properties_instructions": "Modify the style and width of the selected cell.",
	"enter_value": "Please enter a value.",
	"all_layouts": "All Layouts",
	"site_layouts": "Site Layouts",
	"other_layouts": "Other Layouts",
	"create_layout_tooltip_text": "Create a new layout from scratch or import the current layout from the selected page area.",
	"change_layout_preview_width_template": "Layout preview for an area <em>#{width}px</em> wide.",
	"preview_width_tooltip_text": "This width is based on the current width of the Page Studio area used to generate this layout.",
	"layout_name_label": "Name",
	"change_layout_duplicate_name_tip": "A site layout with this name already exists.",
	"create_layout_failed": "There was a problem creating the new layout.",
	"invalid_configuration": "The fields marked * are invalid.",
	"Insert Mark-up Component": "Insert Mark-up Component",
	"imc_tooltip_text": "Use the <em>Insert Mark-up Component</em> tool to create new or reconfigure existing mark-up components.",
	"no_component_selected": "No Component Selected",
	"Description": "Description",
	"Configuration": "Configuration",
	"ps_save_exception": "There was an problem while saving the page layout.\n",
	"ps_save_exception_resource": "The layout item could not be created or retrieved.",
	"ps_save_exception_lock": "The layout item is locked by another user.",
	"ps_save_exception_authorization": "The layout item is not accessible by the current user.",
	"ps_component_exception": "There was a problem retrieving the Page Studio component.",
	"ps_load_components_exception": "There was a problem retrieving the Page Studio components.",
	"ps_load_images_exception": "There was a problem retrieving the Page Studio images.",
	"ps_load_styles_exception": "There was a problem retrieving the Page Studio styles.",
	"ps_load_layouts_exception": "There was a problem retrieving the Page Studio layouts.",
	"ps_load_markup_exception": "There was a problem retrieiving the Mark-up component.",
	"component_error": "Component Error",
	"component_error_click_here": "Click here to view the error.",
	"Unknown_exception": "Exception",
	"Resource_exception": "Resource Exception", 
	"Lock_exception": "Lock Exception",
	"IllegalArgument_exception": "Illegal Argument Exception",
	"Init_exception": "Init Exception", 
	"Servlet_exception": "Servlet Exception",
	"IO_exception": "IO Exception",
	"Authorization_exception": "Authorization Exception",
	"confirm_leave_page": "There are unsaved Page Studio changes.\n\nContinue?",
	"browse": "Browse...",
	"ps_type_name_string": "Text",
	"ps_type_name_int": "Number",
	"ps_type_name_date": "Date",
	"ps_type_name_url": "URL",
	"ps_type_name_british_postcode": "British Postcode",
	"ps_type_name_phone_number": "Phone Number",
	"ps_type_name_markup": "Formatted Text",
	"add_image_info": "Select an image to view it's properties. Double click or drag and drop to add an image to the page.",
	"loading": "Loading...",
	"image_already_exists": "The following images are already attached as inline items:",
	"add_image_tooltip_template": "Add #{name}",
	"cell_properties_preview_option_label": "Preview cell styles",
	"cell_properties_other_label": "General Properties",
	"cell_properties_classes_label": "Classes",
	"cell_properties_selected_classes": "Selected Classes",
	"cell_properties_available_classes": "Available Classes",
	"cell_properties_classes_add": "Add",
	"id": "ID",
	"cell_properties_edit": "Edit",
	"cell_properties_apply": "Apply",
	"edit_ellip": "Edit...",
	"cell_properties_id_invalid": "This ID is already in use. The ID value must be unique.",
	"cell_properties_id_null": "The ID cannot be left blank.",
	"add": "Add...",
	"remove": "Remove",
	"delete_layout": "Delete Layout",
	"delete": "Delete",
	"edit_layout": "Edit Layout",
	"confirm_delete_layout": "Are you sure you want to permenantly delete this layout?",
	"delete_layout_exception": "There was an error while deleting the selected layout.",
	"rename_layout_exception": "There was an error while renaming the selected layout.",
	"delete_selected_layout_tooltip_text": "Delete the selected site layout.",
	"edit_selected_layout_tooltip_text": "Edit the selected site layout.",
	"add_cell": "Add Cell",
	"remove_cell": "Remove Cell",
	"import_current_layout": "Import Current Layout",
	"import_current_layout_tooltip_text": "Import the layout from the currently selected Page Studio area.<br/><br/>This will overwrite the current layout.",
	"confirm_import_layout": "Importing a layout will overwrite the currently show layout.\n\nDo you wish to continue?"
	
};
