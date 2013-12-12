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

var PSCellTab = Class.create(PSToolBar, {
	initParams: function($super, params) {
		$super(params);
		this.cell = params.cell ? params.cell : null;
	},
	
	createElement: function($super, target) {
		$super(target);
		
		this.element.addClassName("ps_cell_toolbar");
		
		this.handle = new Element("div", { className: "ps_item_handle ps_cell_handle" });
		this.element.insert(this.handle);
		
		this.addButton(new PSButton({
			tooltip: { "title": PageStudio.locale.get("Select Component"), "text": PageStudio.locale.get("Select_component_tooltip_text") },
			className: "ps_select_component",
			action: PageStudio.EVENT.DISPLAY_SELECT_COMPONENT_CLICKED,
			memo: { "cell": this.cell }
		}));
		
		this.addButton(new PSButton({
			tooltip: { "title": PageStudio.locale.get("Cell Properties"), "text": PageStudio.locale.get("Cell_properties_tooltip_text") },
			className: "ps_cell_properties_button",
			action: PageStudio.EVENT.DISPLAY_CELL_PROPERTIES_CLICKED,
			memo: { "cell": this.cell }
		}));
		
		this.addButton(new PSButton({
			tooltip: PageStudio.locale.get("Delete Cell"),
			className: "ps_cell_delete_button",
			action: PageStudio.EVENT.DELETE_CELL_CLICKED,
			memo: { "cell": this.cell }
		}));
	}
});