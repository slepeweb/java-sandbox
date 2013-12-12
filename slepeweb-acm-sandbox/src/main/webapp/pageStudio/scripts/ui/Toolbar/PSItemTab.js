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

var PSItemTab = Class.create(PSToolBar, {
	initParams: function($super, params) {
		$super(params);
		this.item = params.item ? params.item : null;
	},
	
	createElement: function($super, target) {
		$super(target);
		
		this.element.addClassName("ps_item_toolbar");
		this.handle = new Element("div", { id: this.item.element.id+"_handle", className: "ps_item_handle ps_field_handle" });
		this.element.insert(this.handle);
		this.deleteButton = new PSButton({
			tooltip: PageStudio.locale.get("Delete Item"),
			className: "ps_delete_item_button",
			action: PageStudio.EVENT.DELETE_CELL_ITEM_CLICKED,
			memo: { item: this.item }
		});
		this.addButton(this.deleteButton);
	}
});

var PSConfigurableComponentTab = Class.create(PSItemTab, {
	createElement: function($super, target) {
		$super();
    this.addButton(new PSButton({ 
    	"className": "ps_display_component_configuration_button", 
    	"tooltip": PageStudio.locale.get("Display Component Configuration"),
    	"action": PSComponent.EVENT.DISPLAY_CONFIGURATION 
    }), this.deleteButton);
	}
}); 

var PSMarkupFieldTab = Class.create(PSItemTab, {
	createElement: function($super, target) {
		$super();
		this.addButton(new PSButton({
			tooltip: { "title": PageStudio.locale.get("Insert Mark-up Component"), "text": PageStudio.locale.get("imc_tooltip_text") },
			className: "ps_insert_markup_component_button",
			action: PageStudio.EVENT.DISPLAY_INSERT_MARKUP_COMPONENT_CLICKED,
			memo: { item: this.item }
		}), this.deleteButton);
	}
});