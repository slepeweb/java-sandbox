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

var PSMainTab = Class.create(PSToolBar, {
	initialize: function($super, params) {
		$super(params);
		this.area = params.area ? params.area : null;
	},
	
	createElement: function($super, target) {
		$super(target);
		
		this.element.addClassName("ps_area_toolbar");
		
		this.addButton(new PSButton({
			tooltip: { "title": PageStudio.locale.get("Select Component"), "text": PageStudio.locale.get("Select_component_tooltip_text") },
			className: "ps_select_component",
			action: PageStudio.EVENT.DISPLAY_SELECT_COMPONENT_CLICKED,
			memo: { item: this.area }
		}));
		
		this.addButton(new PSButton({
			tooltip: { "title": PageStudio.locale.get("Change Layout"), "text": PageStudio.locale.get("Change_layout_tooltip_text") },
			className: "ps_change_layout",
			action: PageStudio.EVENT.DISPLAY_CHANGE_LAYOUT_CLICKED,
			memo: { item: this.area }
		}));
	}
});