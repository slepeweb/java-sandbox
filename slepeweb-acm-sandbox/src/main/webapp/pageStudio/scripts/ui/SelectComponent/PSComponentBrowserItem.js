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

var PSComponentBrowserItem = Class.create(PSBrowserItem, {
	createElement: function($super) {
		$super();
		
		this.element.addClassName("ps_component_list_item");
		this.button.element.addClassName("ps_component_list_item_button");
		
		if(this.component.icon && this.component.icon != "null") {
			var icon = new Element("img", { "className": "ps_icon", "src": this.component.icon });
			this.title.insert({ "top": icon });
			this.title.addClassName("ps_with_icon");
		}

		if(this.component.description) {
			var description = new Element("div", { "className": "ps_description" });
			description.update(this.component.description);
			this.title.insert({ "after": description });
			
		}
		
		if(this.component.fieldType) {
			var typeName;
			switch(this.component.fieldType) 
			{
				case 1:  typeName = "ps_type_name_string"; break;
				case 2:  typeName = "ps_type_name_int"; break;
				case 3:  typeName = "ps_type_name_date"; break;
				case 4:  typeName = "ps_type_name_url"; break;
				case 5:  typeName = "ps_type_name_british_postcode"; break;
				case 6:  typeName = "ps_type_name_phone_number"; break;
				case 7:  typeName = "ps_type_name_markup"; break;
				default: typeName = "";
			}
			var type = new Element("div", { "className": "ps_typename" }).update(PageStudio.locale.get(typeName));
			this.title.addClassName(typeName);
			this.title.insert({ "after": type });
		}
		
		if(this.component.thumbnail && this.component.thumbnail != "null") {
			var thumbnail = new Element("img", { "className": "ps_thumbnail", "src": this.component.thumbnail });
			this.title.insert({ "before": thumbnail });
		}
	}
});